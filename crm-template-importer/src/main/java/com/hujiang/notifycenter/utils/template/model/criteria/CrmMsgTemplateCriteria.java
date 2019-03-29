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
public class CrmMsgTemplateCriteria extends Criteria<Integer> {
    private Integer id;
    private String appName;

    public CrmMsgTemplateCriteria(List<Integer> ids) {
        super(ids);
    }
}
