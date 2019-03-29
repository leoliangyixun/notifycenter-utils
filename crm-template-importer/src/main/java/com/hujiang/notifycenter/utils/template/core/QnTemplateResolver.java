package com.hujiang.notifycenter.utils.template.core;

import com.hujiang.basic.framework.core.util.JsonUtil;
import com.hujiang.notifycenter.common.consts.MsgLevel;
import com.hujiang.notifycenter.qingniao.constants.MsgType;
import com.hujiang.notifycenter.qingniao.constants.WxSubMsgType;
import com.hujiang.notifycenter.qingniao.model.dto.AppPushMessageDto.Notification.Android;
import com.hujiang.notifycenter.qingniao.model.dto.AppPushMessageDto.Notification.Ios;
import com.hujiang.notifycenter.qingniao.model.dto.AppPushMessageDto.Options;
import com.hujiang.notifycenter.qingniao.model.dto.ContentDto.AppPushContentDto;
import com.hujiang.notifycenter.qingniao.model.dto.ContentDto.MailContentDto;
import com.hujiang.notifycenter.qingniao.model.dto.ContentDto.SmsContentDto;
import com.hujiang.notifycenter.qingniao.model.dto.ContentDto.WxContentDto;
import com.hujiang.notifycenter.qingniao.model.dto.ContentDto.WxContentDto.WxMessageContentDto;
import com.hujiang.notifycenter.qingniao.model.dto.TemplateInfoDto;
import com.hujiang.notifycenter.qingniao.model.dto.TemplateListDto;
import com.hujiang.notifycenter.template.common.model.external.EmailBody;
import com.hujiang.notifycenter.template.common.model.external.PushBody;
import com.hujiang.notifycenter.template.common.model.external.WechatTemplateBody;
import com.hujiang.notifycenter.template.common.model.external.WechatTemplateBodyV2;
import com.hujiang.notifycenter.utils.template.dao.QnAppDao;
import com.hujiang.notifycenter.utils.template.dao.QnStrategyDao;
import com.hujiang.notifycenter.utils.template.dao.QnTemplateContentDao;
import com.hujiang.notifycenter.utils.template.dao.QnTemplateGroupDao;
import com.hujiang.notifycenter.utils.template.model.Query;
import com.hujiang.notifycenter.utils.template.model.criteria.QnAppCriteria;
import com.hujiang.notifycenter.utils.template.model.dto.CrmTemplateDto;
import com.hujiang.notifycenter.utils.template.model.dto.CrmTemplateGroupDto;
import com.hujiang.notifycenter.utils.template.model.po.QnAppPo;
import com.hujiang.notifycenter.utils.template.model.po.QnStrategyPo;
import com.hujiang.notifycenter.utils.template.model.po.QnTemplateContentPo;
import com.hujiang.notifycenter.utils.template.model.po.QnTemplateGroupPo;
import com.hujiang.notifycenter.utils.template.support.MsgLevelConverter;
import com.hujiang.notifycenter.utils.template.support.MsgTypeConverter;
import com.hujiang.notifycenter.wechat.support.model.dto.WXMessage2.MiniProgram;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class QnTemplateResolver {

    @Autowired
    private QnAppDao appDao;

    @Autowired
    private QnStrategyDao strategyDao;

    @Autowired
    private QnTemplateContentDao templateContentDao;

    @Autowired
    private QnTemplateGroupDao templateGroupDao;

    private final LoadingCache<String, QnAppPo> APP_CACHE = CacheBuilder
        .newBuilder().expireAfterWrite(10, TimeUnit.MINUTES)
        .build(new CacheLoader<String, QnAppPo>() {
            @Override
            public QnAppPo load(String name) {
                List<QnAppPo> list = appDao.query(new Query<>(new QnAppCriteria(name)));
                return list.get(0);
            }
        });

    public void resolve(List<CrmTemplateGroupDto> list) {
        for (CrmTemplateGroupDto e : list) {
            if (CollectionUtils.isNotEmpty(e.getContents())) {
                boolean isValid = e.getContents().stream().anyMatch(k -> Objects.nonNull(k.getContent()));
                if (isValid) {
                    QnAppPo app = APP_CACHE.getUnchecked(e.getTemplate().getAppName());
                    MsgLevel msgLevel = MsgLevelConverter.getMsgLevel(e.getTemplate().getLevel());
                    //第1步：保存策略
                    QnStrategyPo strategy = new QnStrategyPo();
                    strategy.setAppKey(app.getAppKey());
                    strategy.setTitle(e.getTemplate().getTitle());
                    strategy.setMsgLevel(msgLevel);
                    strategy.setDescription("CRM导入[" + e.getTemplateId() + "]");
                    strategy.setStatus(200);
                    strategyDao.insert(strategy);

                    //第2步：保存模板
                    List<Integer> templateIds = null;
                    List<TemplateListDto> optionalTemplates = null;

                    List<Integer> crmRequiredTemplateContentIds = Lists.newArrayList();
                    List<Integer> crmOptionalTemplateContentIds = Lists.newArrayList();
                    //根据优先级和是否必发进行排序
                    List<CrmTemplateDto> contents = e.getContents().stream()
                        .sorted((o1, o2) -> Integer.compare(o1.getRule().getSortBy(), o2.getRule().getSortBy()))
                        .sorted((o1, o2) -> Boolean.compare(o2.getRule().getIsNecessary(), o1.getRule().getIsNecessary()))
                        .collect(Collectors.toList());

                    for (CrmTemplateDto k : contents) {
                        if (k.getContent() != null) {
                            QnTemplateContentPo content = new QnTemplateContentPo();
                            MsgType msgType = MsgTypeConverter.getMsgType(k.getContent().getType());
                            content.setAppKey(app.getAppKey());
                            content.setMsgLevel(msgLevel);
                            content.setTitle("CRM导入[" + k.getContent().getId() + "]");
                            content.setMsgType(msgType);
                            content.setStatus(200);

                            if (msgType == MsgType.sms) {
                                try {
                                    SmsContentDto sms = new SmsContentDto();
                                    sms.setSign(msgLevel == MsgLevel.vcode ? "【沪江】" : "【沪江网校】");
                                    String text = k.getContent().getContent();
                                    text = StringUtils.replace(text, "【", "(");
                                    text = StringUtils.replace(text, "】", ")");
                                    sms.setContent(text);
                                    sms.setIsOversea(false);
                                    content.setContent(sms.toString());
                                } catch (Exception ex) {
                                    log.error("error", ex);
                                    log.error("短信模板解析异常, 模板: {}", k.getContent());
                                }
                            } else if (msgType == MsgType.wechat) {
                                try {
                                    Map<String, Object> result = JsonUtil.json2Map(k.getContent().getContent());
                                    if (result.containsKey("version") && result.get("version").equals("v2")) {
                                        WechatTemplateBodyV2 body = JsonUtil.json2Object(k.getContent().getContent(), WechatTemplateBodyV2.class);
                                        WxMessageContentDto template = new WxMessageContentDto();
                                        template.setTemplateId(body.getTemplateId());
                                        template.setUrl(body.getUrl());
                                        if (body.getMiniprogram() != null) {
                                            MiniProgram miniprogram = new MiniProgram();
                                            miniprogram.setAppid(body.getMiniprogram().getAppid());
                                            miniprogram.setPagepath(body.getMiniprogram().getPagepath());
                                            template.setMiniprogram(miniprogram);
                                        }

                                        WxContentDto wx = new WxContentDto();
                                        wx.setWechatAppId(body.getWechatAppId());
                                        wx.setWxMsgType(WxSubMsgType.tmpl);
                                        wx.setTemplate(template);

                                        content.setContent(wx.toString());
                                    } else {
                                        WechatTemplateBody body = JsonUtil.json2Object(k.getContent().getContent(), WechatTemplateBody.class);
                                        WxMessageContentDto template = new WxMessageContentDto();
                                        template.setTemplateId(body.getTemplate_id());
                                        template.setUrl(body.getUrl());
                                        if (body.getMiniprogram() != null) {
                                            MiniProgram miniprogram = new MiniProgram();
                                            miniprogram.setAppid(body.getMiniprogram().getAppid());
                                            miniprogram.setPagepath(body.getMiniprogram().getPagepath());
                                            template.setMiniprogram(miniprogram);
                                        }

                                        WxContentDto wx = new WxContentDto();
                                        wx.setWechatAppId(body.getWechatAppId());
                                        wx.setWxMsgType(WxSubMsgType.tmpl);
                                        wx.setTemplate(template);

                                        content.setContent(wx.toString());
                                    }
                                } catch (Exception ex) {
                                    log.error("error", ex);
                                    log.error("微信模板解析异常, 模板: {}", k.getContent());
                                }
                            } else if (msgType == MsgType.apppush) {
                                try {
                                    PushBody body = JsonUtil.json2Object(k.getContent().getContent(), PushBody.class);

                                    AppPushContentDto apppush = new AppPushContentDto();
                                    apppush.setAppName(body.getAppname());

                                    Android android = new Android();
                                    if (body.getMessage() != null) {
                                        android.setTitle(body.getMessage().getTitle());
                                        android.setAlert(body.getMessage().getContent());
                                        android.setAction(body.getMessage().getAction());
                                        android.setIsJpushNotification(false);
                                        android.setMode(body.getMessage().getMode());
                                        android.setTrack(body.getMessage().getTrack());
                                        android.setFlags(body.getMessage().getFlags());
                                        apppush.setAndroid(android);
                                    }
                                    if (body.getNotification() != null) {
                                        Ios ios = new Ios();
                                        ios.setTitle(null);
                                        ios.setAlert(body.getNotification().getAlert());
                                        ios.setBadge(body.getNotification().getBadge());
                                        ios.setAction(body.getNotification().getAction());
                                        ios.setMode(body.getNotification().getMode());
                                        ios.setSound(body.getNotification().getSound());
                                        ios.setTrack(body.getNotification().getTrack());
                                        apppush.setIos(ios);
                                    }

                                    if (body.getOptions() != null) {
                                        Options option = new Options();
                                        option.setTimeToLive((long) body.getOptions().getTime_to_live());
                                        option.setOverrideMsgId((long) body.getOptions().getOverride_msg_id());
                                        option.setApnsCollapseId(null);
                                        apppush.setOptions(option);
                                    }

                                    content.setContent(apppush.toString());
                                } catch (Exception ex) {
                                    log.error("error", ex);
                                    log.error("应用推送模板解析异常, 模板: {}", k.getContent());
                                }
                            } else if (msgType == MsgType.mail) {
                                try {
                                    EmailBody body = JsonUtil.json2Object(k.getContent().getContent(), EmailBody.class);
                                    MailContentDto mail = new MailContentDto();
                                    mail.setIsInternal(false);
                                    mail.setSubject(body.getSubject());
                                    mail.setTemplet(body.getTemplet());

                                    content.setContent(mail.toString());
                                } catch (Exception ex) {
                                    log.error("error", ex);
                                    log.error("邮件模板解析异常, 模板: {}", k.getContent());
                                }
                            }

                            templateContentDao.insert(content);

                            if (k.getRule().getIsNecessary()) {
                                //必发模板
                                if (CollectionUtils.isEmpty(templateIds)) {
                                    templateIds = Lists.newArrayList();
                                }
                                templateIds.add(content.getId());
                                crmRequiredTemplateContentIds.add(k.getContent().getId());
                            } else {
                                TemplateListDto dto = new TemplateListDto();
                                dto.setTemplateId(content.getId());
                                dto.setPriority(k.getRule().getSortBy());
                                if (CollectionUtils.isEmpty(optionalTemplates)) {
                                    optionalTemplates = Lists.newArrayList();
                                }
                                optionalTemplates.add(dto);
                                crmOptionalTemplateContentIds.add(k.getContent().getId());
                            }
                        }
                    }

                    //第3步：保存模板组
                    List<TemplateListDto> requiredTemplates = Lists.newArrayList();
                    if (CollectionUtils.isNotEmpty(templateIds) && CollectionUtils.isNotEmpty(optionalTemplates)) {
                        //有必发, 有补发
                        //由于老的模板消息必发与补发是隔离的，先发所有的必发，必发都失败的用户再根据优先级补发
                        //消息中心2.0为了尽可能兼容老的模板消息,将补发模板作为最后一个必发模板对应的补发模板
                        for (int i = 0 ;i < templateIds.size(); i++) {
                            TemplateListDto templateList = new TemplateListDto();
                            templateList.setTemplateId(templateIds.get(i));
                            if (i == templateIds.size() - 1) {
                                templateList.setOptionalTemplates(optionalTemplates);
                            }
                            requiredTemplates.add(templateList);
                        }
                    } else {
                        //必发
                        if (CollectionUtils.isNotEmpty(templateIds)) {
                            for (Integer templateId : templateIds) {
                                TemplateListDto templateList = new TemplateListDto();
                                templateList.setTemplateId(templateId);
                                requiredTemplates.add(templateList);
                            }
                        }
                        //补发
                        if (CollectionUtils.isNotEmpty(optionalTemplates)) {
                            TemplateListDto templateList = new TemplateListDto();
                            templateList.setOptionalTemplates(optionalTemplates);
                            requiredTemplates.add(templateList);
                        }
                    }

                    TemplateInfoDto templateInfo = new TemplateInfoDto();
                    templateInfo.setRequiredTemplates(requiredTemplates);

                    QnTemplateGroupPo group = new QnTemplateGroupPo();
                    group.setAppKey(app.getAppKey());
                    group.setStrategyId(strategy.getId());
                    group.setTitle(
                        "CRM导入模板[" + e.getTemplateId() + "(" + e.getTemplate().getId() + ")]-必发[" + StringUtils
                            .join(crmRequiredTemplateContentIds, ",") + "]-补发[" + StringUtils
                            .join(crmOptionalTemplateContentIds, ",") + "]");
                    group.setTemplateInfo(templateInfo);
                    group.setStatus(200);
                    templateGroupDao.insert(group);
                }
            }
        }
    }

}
