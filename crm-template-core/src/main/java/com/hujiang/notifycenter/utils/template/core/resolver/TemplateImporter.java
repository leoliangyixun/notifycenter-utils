package com.hujiang.notifycenter.utils.template.core.resolver;

import com.hujiang.notifycenter.common.consts.MsgLevel;
import com.hujiang.notifycenter.qingniao.constants.MsgType;
import com.hujiang.notifycenter.qingniao.model.dto.TemplateDto;
import com.hujiang.notifycenter.qingniao.model.dto.TemplateInfoDto;
import com.hujiang.notifycenter.utils.template.core.dao.QnAppDao;
import com.hujiang.notifycenter.utils.template.core.dao.QnTemplateContentDao;
import com.hujiang.notifycenter.utils.template.core.dao.QnTemplateGroupDao;
import com.hujiang.notifycenter.utils.template.core.extractor.AppPushExtractor;
import com.hujiang.notifycenter.utils.template.core.extractor.MailExtractor;
import com.hujiang.notifycenter.utils.template.core.extractor.SmsExtractor;
import com.hujiang.notifycenter.utils.template.core.extractor.WxExtractor;
import com.hujiang.notifycenter.utils.template.core.model.Query;
import com.hujiang.notifycenter.utils.template.core.model.dto.CrmMsgTemplateDto;
import com.hujiang.notifycenter.utils.template.core.model.po.QnAppPo;
import com.hujiang.notifycenter.utils.template.core.model.po.QnStrategyPo;
import com.hujiang.notifycenter.utils.template.core.model.po.QnTemplateContentPo;
import com.hujiang.notifycenter.utils.template.core.model.po.QnTemplateGroupPo;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import com.hujiang.notifycenter.utils.template.core.dao.QnStrategyDao;
import com.hujiang.notifycenter.utils.template.core.model.criteria.QnAppCriteria;
import com.hujiang.notifycenter.utils.template.core.model.dto.CrmTemplateContentDto;
import com.hujiang.notifycenter.utils.template.core.support.MsgLevelConverter;
import com.hujiang.notifycenter.utils.template.core.support.MsgTypeConverter;

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
public abstract class TemplateImporter {

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
                List<QnAppPo> list = appDao.query(new Query<>(new QnAppCriteria().name(name).build()));
                return list.get(0);
            }
        });

    public abstract void execute(List<Integer> requiredTemplateIds, List<TemplateDto> optionalTemplates, List<TemplateDto> requiredTemplates);

    public void resolve(List<CrmMsgTemplateDto> list) {
        for (CrmMsgTemplateDto e : list) {
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

                    List<TemplateDto> qnOptionalTemplates = Lists.newArrayList();
                    List<TemplateDto> qnRequiredTemplates = Lists.newArrayList();

                    List<Integer> crmRequiredTemplateIds = Lists.newArrayList();
                    List<Integer> crmOptionalTemplateIds = Lists.newArrayList();
                    //根据优先级和是否必发进行排序
                    List<CrmTemplateContentDto> contents = e.getContents().stream()
                        .sorted((o1, o2) -> Integer.compare(o1.getRule().getSortBy(), o2.getRule().getSortBy()))
                        .sorted((o1, o2) -> Boolean.compare(o2.getRule().getIsNecessary(), o1.getRule().getIsNecessary()))
                        .collect(Collectors.toList());

                    for (CrmTemplateContentDto crm : contents) {
                        if (crm.getContent() != null) {
                            QnTemplateContentPo qn = new QnTemplateContentPo();
                            MsgType msgType = MsgTypeConverter.getMsgType(crm.getContent().getType());
                            qn.setAppKey(app.getAppKey());
                            qn.setMsgLevel(msgLevel);
                            qn.setTitle("CRM导入[" + crm.getContent().getId() + "]");
                            qn.setMsgType(msgType);
                            qn.setStatus(200);

                            if (msgType == MsgType.sms) {
                                new SmsExtractor().extract(msgLevel, crm, qn);
                            } else if (msgType == MsgType.wechat) {
                                new WxExtractor().extract(crm, qn);
                            } else if (msgType == MsgType.apppush) {
                                new AppPushExtractor().extract(crm, qn);
                            } else if (msgType == MsgType.mail) {
                                new MailExtractor().extract(crm, qn);
                            }

                            templateContentDao.insert(qn);

                            if (crm.getRule().getIsNecessary()) {
                                //必发模板
                                qnRequiredTemplateIds.add(qn.getId());
                                crmRequiredTemplateIds.add(crm.getContent().getId());
                            } else {
                                TemplateDto dto = new TemplateDto();
                                dto.setTemplateId(qn.getId());
                                dto.setPriority(crm.getRule().getSortBy());

                                qnOptionalTemplates.add(dto);
                                crmOptionalTemplateIds.add(crm.getContent().getId());
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
