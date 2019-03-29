package com.hujiang.notifycenter.utils.template.core;

import com.hujiang.basic.framework.dao.annotation.TargetDataSource;
import com.hujiang.notifycenter.template.common.Helper;
import com.hujiang.notifycenter.utils.template.dao.CrmMsgTemplateDao;
import com.hujiang.notifycenter.utils.template.dao.CrmTemplateContentDao;
import com.hujiang.notifycenter.utils.template.dao.CrmTemplateRuleDao;
import com.hujiang.notifycenter.utils.template.model.Query;
import com.hujiang.notifycenter.utils.template.model.criteria.CrmMsgTemplateCriteria;
import com.hujiang.notifycenter.utils.template.model.criteria.CrmTemplateContentCriteria;
import com.hujiang.notifycenter.utils.template.model.criteria.CrmTemplateRuleCriteria;
import com.hujiang.notifycenter.utils.template.model.dto.CrmTemplateDto;
import com.hujiang.notifycenter.utils.template.model.dto.CrmTemplateGroupDto;
import com.hujiang.notifycenter.utils.template.model.po.CrmMsgTemplatePo;
import com.hujiang.notifycenter.utils.template.model.po.CrmTemplateContentPo;
import com.hujiang.notifycenter.utils.template.model.po.CrmTemplateRulePo;

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

@Component
public class CrmTemplateResolver {

    @Autowired
    private CrmMsgTemplateDao crmMsgTemplateDao;
    @Autowired
    private CrmTemplateRuleDao crmTemplateRuleDao;
    @Autowired
    private CrmTemplateContentDao crmTemplateContentDao;

    @TargetDataSource("template")
    public List<CrmTemplateGroupDto> resolve(List<Integer> ids) {
        List<CrmMsgTemplatePo> list1 = crmMsgTemplateDao.query(new Query<>(new CrmMsgTemplateCriteria(ids)));
        List<CrmTemplateRulePo> list2 = crmTemplateRuleDao.query(new Query<>(new CrmTemplateRuleCriteria(ids)));

        if (CollectionUtils.isNotEmpty(list1) && CollectionUtils.isNotEmpty(list2)) {
            //key:templateId
            Map<Integer, List<CrmTemplateRulePo>> map1 = list2.stream().collect(Collectors.groupingBy((e) -> e.getTemplateId()));
            //key:templateId
            Map<Integer, List<CrmTemplateContentPo>> map2 = map1.entrySet().stream()
                .collect(Collectors.toMap((k) -> k.getKey(), (v) -> {
                    List<Integer> templateContentIds = v.getValue().stream().map(e -> e.getTemplateContentId()).collect(Collectors.toList());
                    List<CrmTemplateContentPo> list = crmTemplateContentDao.query(new Query<>(new CrmTemplateContentCriteria(templateContentIds)));
                    return list;
                }));
            //key:templateId
            Map<Integer, CrmMsgTemplatePo> map3 = list1.stream().collect(Collectors.toMap((k) -> k.getId(), (v) -> v));

            List<CrmTemplateGroupDto> list = map1.entrySet().stream().map(e -> {
                CrmTemplateGroupDto group = new CrmTemplateGroupDto();
                try {
                    //key:templateContentId
                    Map<Integer, CrmTemplateRulePo> dict1 = e.getValue().stream().collect(Collectors.toMap((k) -> k.getTemplateContentId(), (v) -> v));
                    //key:templateContentId
                    Map<Integer, CrmTemplateContentPo> dict2 = map2.get(e.getKey()).stream().collect(Collectors.toMap((k) -> k.getId(), (v) -> v));

                    List<CrmTemplateDto> contents = e.getValue().stream().map(r -> {
                        CrmTemplateDto content = new CrmTemplateDto();
                        content.setRule(r);
                        content.setContent(dict2.get(r.getTemplateContentId()));
                        return content;
                    }).collect(Collectors.toList());

                    group.setTemplateId(Helper.encrypt(String.valueOf(e.getKey())));
                    group.setTemplate(map3.get(e.getKey()));
                    group.setContents(contents);

                } catch (InvalidKeyException e1) {
                    e1.printStackTrace();
                } catch (InvalidKeySpecException e1) {
                    e1.printStackTrace();
                } catch (NoSuchAlgorithmException e1) {
                    e1.printStackTrace();
                } catch (NoSuchPaddingException e1) {
                    e1.printStackTrace();
                } catch (InvalidAlgorithmParameterException e1) {
                    e1.printStackTrace();
                } catch (IllegalBlockSizeException e1) {
                    e1.printStackTrace();
                } catch (BadPaddingException e1) {
                    e1.printStackTrace();
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }

                return group;
            }).collect(Collectors.toList());

            return list;
        }

        return null;
    }

}
