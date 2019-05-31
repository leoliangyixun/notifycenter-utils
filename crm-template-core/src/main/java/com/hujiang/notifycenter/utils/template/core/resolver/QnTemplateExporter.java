package com.hujiang.notifycenter.utils.template.core.resolver;
import com.hujiang.notifycenter.qingniao.constants.MsgType;
import com.hujiang.notifycenter.qingniao.model.dto.TemplateInfoDto;
import com.hujiang.notifycenter.common.consts.MsgLevel;

import com.hujiang.basic.framework.dao.annotation.TargetDataSource;
import com.hujiang.notifycenter.qingniao.model.dto.TemplateDto;
import com.hujiang.notifycenter.utils.template.core.dao.QnStrategyDao;
import com.hujiang.notifycenter.utils.template.core.dao.QnTemplateContentDao;
import com.hujiang.notifycenter.utils.template.core.dao.QnTemplateGroupDao;
import com.hujiang.notifycenter.utils.template.core.model.Query;
import com.hujiang.notifycenter.utils.template.core.model.criteria.QnTemplateCriteria;
import com.hujiang.notifycenter.utils.template.core.model.dto.QnStrategyDto;
import com.hujiang.notifycenter.utils.template.core.model.po.QnStrategyPo;
import com.hujiang.notifycenter.utils.template.core.model.po.QnTemplateContentPo;
import com.hujiang.notifycenter.utils.template.core.model.po.QnTemplateGroupPo;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class QnTemplateExporter {
    private static final String STRATEGY = "strategy";

    @Autowired
    private QnStrategyDao qnStrategyDao;

    @Autowired
    private QnTemplateGroupDao qnTemplateGroupDao;

    @Autowired
    private QnTemplateContentDao qnTemplateContentDao;

    private final LoadingCache<String, List<QnStrategyPo>> CACHE = CacheBuilder
            .newBuilder().expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<String, List<QnStrategyPo>>() {

                @Override
                public List<QnStrategyPo> load(String key) {
                    return qnStrategyDao.queryAll();
                }
            });

    @TargetDataSource("qingniao")
    public List<QnStrategyPo> findByDescription(String description) {
        List<QnStrategyPo> list = CACHE.getUnchecked(STRATEGY);
        List<QnStrategyPo> strategies = list.stream().filter(e -> StringUtils.contains(e.getDescription(), description)).collect(Collectors.toList());
        return strategies;
    }

    @TargetDataSource("qingniao")
    public List<QnStrategyPo> findByAppKey(String appKey) {
        List<QnStrategyPo> list = CACHE.getUnchecked(STRATEGY);
        List<QnStrategyPo> strategies = list.stream().filter(e -> Objects.equals(e.getAppKey(), appKey)).collect(Collectors.toList());
        return strategies;
    }

    @TargetDataSource("qingniao")
    public void copyByAppKey(String targetAppKey, String[] sourceAppKeys) {
        for (String appKey: sourceAppKeys) {
            List<QnStrategyPo> list = this.findByAppKey(appKey);
            if (CollectionUtils.isNotEmpty(list)) {
                List<Integer> sids = list.stream().map(e -> e.getId()).collect(Collectors.toList());
                List<QnTemplateGroupPo> list1 = qnTemplateGroupDao.query(new Query<>(new QnTemplateCriteria().sids(sids).build()));
                if (CollectionUtils.isNotEmpty(list1)) {
                    Map<Integer, QnTemplateGroupPo> map = list1.stream().collect(Collectors.toMap((k) -> k.getStrategyId(), (v) -> v));
                    list.forEach(strategy -> {
                        //第一步：复制策略
                        QnStrategyPo _strategy = new QnStrategyPo();
                        _strategy.setAppKey(targetAppKey);
                        _strategy.setTitle(strategy.getTitle());
                        _strategy.setMsgLevel(strategy.getMsgLevel());
                        _strategy.setDescription(strategy.getDescription());
                        _strategy.setStatus(strategy.getStatus());

                        qnStrategyDao.insert(_strategy);
                        //第二步：复制模板组
                        QnTemplateGroupPo group = map.get(strategy.getId());

                        QnTemplateGroupPo _group = new QnTemplateGroupPo();
                        _group.setAppKey(appKey);
                        _group.setStrategyId(_strategy.getId());
                        _group.setTitle(group.getTitle());
                        _group.setTemplateInfo(group.getTemplateInfo());
                        _group.setDescription(group.getDescription());
                        _group.setStatus(group.getStatus());

                        qnTemplateGroupDao.insert(_group);
                        //第三步：复制模板
                        List<TemplateDto> requiredTemplates = _group.getTemplateInfo().getRequiredTemplates();
                        if (CollectionUtils.isNotEmpty(requiredTemplates)) {
                            List<Integer> cids = requiredTemplates.stream()
                                .reduce(new ArrayList<>(10), (t, u) -> {
                                    t.add(u.getTemplateId());
                                    if (CollectionUtils.isNotEmpty(u.getOptionalTemplates())) {
                                        t.addAll(u.getOptionalTemplates()
                                            .stream()
                                            .filter(e -> e.getTemplateId() != null)
                                            .collect(Collectors.mapping(e -> e.getTemplateId(), Collectors.toList()))
                                        );
                                    }
                                    return t;
                                }, (t, u) -> t);

                            List<QnTemplateContentPo> list2 = qnTemplateContentDao.query(new Query<>(new QnTemplateCriteria().cids(cids).build()));
                            if (CollectionUtils.isNotEmpty(list2)) {
                                List<QnTemplateContentPo> list3 = Lists.newArrayList();
                                list2.forEach(content -> {
                                    QnTemplateContentPo _content = new QnTemplateContentPo();
                                    _content.setAppKey(appKey);
                                    _content.setMsgLevel(content.getMsgLevel());
                                    _content.setTitle(content.getTitle());
                                    _content.setMsgType(content.getMsgType());
                                    _content.setContent(content.getContent());
                                    _content.setStatus(content.getStatus());
                                    list3.add(_content);
                                });

                                qnTemplateContentDao.insertBatch(list3);
                            }
                        }

                    });
                }
            }
        }
    }

}
