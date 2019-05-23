package com.hujiang.notifycenter.utils.template.core;

import com.hujiang.notifycenter.common.consts.MsgLevel;
import com.hujiang.notifycenter.qingniao.constants.MsgType;
import com.hujiang.notifycenter.qingniao.model.dto.TemplateInfoDto;
import com.hujiang.notifycenter.qingniao.model.dto.TemplateListDto;
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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author yangkai
 * @date 2019-04-03
 * @email yangkai@hujiang.com
 * @description
 */
@Slf4j
public abstract class TemplateResolver {

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

    public abstract void execute(List<Integer> requiredTemplateIds, List<TemplateListDto> optionalTemplates, List<TemplateListDto> requiredTemplates);

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
                    strategy.setDescription("CRM导入[" + e.getTemplateId() + ":"+ e.getIntTemplateId()+ "]");
                    strategy.setStatus(200);
                    strategyDao.insert(strategy);

                    //第2步：保存模板
                    List<Integer> qnRequiredTemplateIds = Lists.newArrayList();

                    List<TemplateListDto> qnOptionalTemplates = Lists.newArrayList();
                    List<TemplateListDto> qnRequiredTemplates = Lists.newArrayList();

                    List<Integer> crmRequiredTemplateIds = Lists.newArrayList();
                    List<Integer> crmOptionalTemplateIds = Lists.newArrayList();
                    //根据优先级和是否必发进行排序
                    List<CrmTemplateDto> contents = e.getContents().stream()
                        .sorted((o1, o2) -> Integer.compare(o1.getRule().getSortBy(), o2.getRule().getSortBy()))
                        .sorted((o1, o2) -> Boolean.compare(o2.getRule().getIsNecessary(), o1.getRule().getIsNecessary()))
                        .collect(Collectors.toList());

                    for (CrmTemplateDto k : contents) {
                        boolean failed = false;
                        if (k.getContent() != null) {
                            QnTemplateContentPo content = new QnTemplateContentPo();
                            MsgType msgType = MsgTypeConverter.getMsgType(k.getContent().getType());
                            content.setAppKey(app.getAppKey());
                            content.setMsgLevel(msgLevel);
                            content.setTitle("CRM导入[" + k.getContent().getId() + "]");
                            content.setMsgType(msgType);
                            content.setStatus(200);

                            if (msgType == MsgType.sms) {
                                new SmsExtractor(msgLevel, k, content).extract();
                            } else if (msgType == MsgType.wechat) {
                                new WxExtractor(k, content).extract();
                            } else if (msgType == MsgType.apppush) {
                                new AppPushExtractor(k, content).extract();
                            } else if (msgType == MsgType.mail) {
                                new MailExtractor(k, content).extract();
                            }

                            templateContentDao.insert(content);

                            if (k.getRule().getIsNecessary()) {
                                //必发模板
                                qnRequiredTemplateIds.add(content.getId());
                                crmRequiredTemplateIds.add(k.getContent().getId());
                            } else {
                                TemplateListDto dto = new TemplateListDto();
                                dto.setTemplateId(content.getId());
                                dto.setPriority(k.getRule().getSortBy());

                                qnOptionalTemplates.add(dto);
                                crmOptionalTemplateIds.add(k.getContent().getId());
                            }
                        }
                    }

                    //第3步：保存模板组
                    this.execute(qnRequiredTemplateIds, qnOptionalTemplates, qnRequiredTemplates);

                    TemplateInfoDto templateInfo = new TemplateInfoDto();
                    templateInfo.setRequiredTemplates(qnRequiredTemplates);

                    QnTemplateGroupPo group = new QnTemplateGroupPo();
                    group.setAppKey(app.getAppKey());
                    group.setStrategyId(strategy.getId());
                    group.setTitle("CRM导入[" + e.getTemplateId() + ":" + e.getTemplate().getId() + "]"
                      + "-必发[" + StringUtils.join(crmRequiredTemplateIds, ",") + "]"
                      + "-补发[" + StringUtils.join(crmOptionalTemplateIds, ",") + "]");
                    group.setTemplateInfo(templateInfo);
                    group.setStatus(200);
                    templateGroupDao.insert(group);
                }
            }
        }
    }

}
