package com.hujiang.notifycenter.utils.template.core.resolver;

import com.hujiang.basic.framework.core.util.JsonUtil;
import com.hujiang.basic.framework.dao.annotation.TargetDataSource;
import com.hujiang.notifycenter.template.common.Helper;
import com.hujiang.notifycenter.utils.template.core.dao.CrmMsgTemplateDao;
import com.hujiang.notifycenter.utils.template.core.dao.CrmTemplateContentDao;
import com.hujiang.notifycenter.utils.template.core.dao.CrmTemplateGroupDao;
import com.hujiang.notifycenter.utils.template.core.dao.CrmTemplateRuleDao;
import com.hujiang.notifycenter.utils.template.core.model.Query;
import com.hujiang.notifycenter.utils.template.core.model.criteria.CrmTemplateCriteria;
import com.hujiang.notifycenter.utils.template.core.model.dto.CrmMsgTemplateDto;
import com.hujiang.notifycenter.utils.template.core.model.dto.CrmTemplateContentDto;
import com.hujiang.notifycenter.utils.template.core.model.dto.QnStrategyDto;
import com.hujiang.notifycenter.utils.template.core.model.po.CrmMsgTemplatePo;
import com.hujiang.notifycenter.utils.template.core.model.po.CrmTemplateContentPo;
import com.hujiang.notifycenter.utils.template.core.model.po.CrmTemplateGroupPo;
import com.hujiang.notifycenter.utils.template.core.model.po.CrmTemplateRulePo;
import com.hujiang.notifycenter.utils.template.core.model.po.QnStrategyPo;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CrmTemplateExporter {

    @Autowired
    private CrmTemplateGroupDao crmTemplateGroupDao;
    @Autowired
    private CrmMsgTemplateDao crmMsgTemplateDao;
    @Autowired
    private CrmTemplateRuleDao crmTemplateRuleDao;
    @Autowired
    private CrmTemplateContentDao crmTemplateContentDao;

    @Autowired
    private QnTemplateExporter qnTemplateExporter;

    @TargetDataSource("template")
    public List<CrmMsgTemplateDto> export(List<Integer> tids) {
        //模板消息模板
        List<CrmMsgTemplatePo> list1 = crmMsgTemplateDao.query(new Query<>(new CrmTemplateCriteria().tids(tids).build()));
        //模板消息模板&模板内容
        List<CrmTemplateRulePo> list2 = crmTemplateRuleDao.query(new Query<>(new CrmTemplateCriteria().tids(tids).build()));

        if (CollectionUtils.isNotEmpty(list1) && CollectionUtils.isNotEmpty(list2)) {
            //key:templateId
            Map<Integer, List<CrmTemplateRulePo>> map1 = list2.stream().collect(Collectors.groupingBy((e) -> e.getTemplateId()));
            //key:templateId
            Map<Integer, List<CrmTemplateContentPo>> map2 = map1.entrySet().stream()
                .collect(Collectors.toMap((k) -> k.getKey(), (v) -> {
                    List<Integer> cids = v.getValue().stream().map(e -> e.getTemplateContentId()).collect(Collectors.toList());
                    List<CrmTemplateContentPo> list = crmTemplateContentDao.query(new Query<>(new CrmTemplateCriteria().cids(cids).build()));
                    return list;
                }));
            //key:templateId
            Map<Integer, CrmMsgTemplatePo> map3 = list1.stream().collect(Collectors.toMap((k) -> k.getId(), (v) -> v));
            List<CrmMsgTemplateDto> list = map1.entrySet().stream().map((Map.Entry<Integer, List<CrmTemplateRulePo>> e) -> {
                CrmMsgTemplateDto group = new CrmMsgTemplateDto();
                try {
                    //key:templateContentId
                    Map<Integer, CrmTemplateContentPo> dict = map2.get(e.getKey()).stream().collect(Collectors.toMap((k) -> k.getId(), (v) -> v));
                    List<CrmTemplateContentDto> contents = e.getValue().stream().map(r -> {
                        CrmTemplateContentDto content = new CrmTemplateContentDto();
                        content.setRule(r);
                        content.setContent(dict.get(r.getTemplateContentId()));
                        return content;
                    }).collect(Collectors.toList());

                    group.setTemplateId(Helper.encrypt(String.valueOf(e.getKey())));
                    group.setTemplate(map3.get(e.getKey()));
                    group.setContents(contents);
                } catch (InvalidKeyException | UnsupportedEncodingException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
                    ex.printStackTrace();
                }

                return group;
            }).collect(Collectors.toList());

            return list;
        }

        return null;
    }

    @TargetDataSource("template")
    public List<CrmMsgTemplateDto> exportAll() {
        List<CrmTemplateGroupPo> list = crmTemplateGroupDao.query(new Query<>(new CrmTemplateCriteria()));
        log.info("crm templates: {}", JsonUtil.object2JSON(list));

        if (CollectionUtils.isNotEmpty(list)) {
            List<CrmMsgTemplateDto> templates = Lists.newArrayList();
            Map<Integer, List<CrmTemplateGroupPo>> map = list.stream().collect(Collectors.groupingBy((e) -> e.getTemplateId(), Collectors.toList()));
            map.forEach((k, v) -> {
                CrmMsgTemplateDto dto = new CrmMsgTemplateDto();

                try {
                    dto.setTemplateId(Helper.encrypt(String.valueOf(k)));
                    dto.setTemplate(new CrmMsgTemplatePo(v.get(0).getTemplateId(), v.get(0).getTitle(), v.get(0).getAppName(), v.get(0).getLevel()));
                    dto.setContents(v.stream().map((e) -> new CrmTemplateContentDto(
                        new CrmTemplateRulePo(e.getTemplateId(), e.getTemplateContentId(), e.getSortBy(), e.getIsNecessary(), e.getAppId()),
                        new CrmTemplateContentPo(e.getTemplateContentId(), e.getContent(), e.getType())
                    )).collect(Collectors.toList()));

                    //看看2.0是否导入过该模板
                    List<QnStrategyPo> strategies = qnTemplateExporter.findByDescription(dto.getTemplateId());
                    if (CollectionUtils.isEmpty(strategies)) {
                        templates.add(dto);
                    }
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            });

            return templates;
        }

        return null;
    }

}
