package com.hujiang.notifycenter.utils.template.model.criteria;

import com.hujiang.notifycenter.utils.template.model.Criteria;

import lombok.Data;

import java.util.List;

/**
 * @author yangkai
 * @date 2019-03-25
 * @email yangkai@hujiang.com
 * @description
 */
@Data
public class CrmTemplateRuleCriteria extends Criteria<Integer> {
    private Integer templateId;
    private Integer templateContentId;

    public CrmTemplateRuleCriteria(List<Integer> ids) {
        super(ids);
    }
}
