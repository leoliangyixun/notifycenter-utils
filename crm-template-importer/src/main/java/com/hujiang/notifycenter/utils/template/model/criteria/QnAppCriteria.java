package com.hujiang.notifycenter.utils.template.model.criteria;

import com.hujiang.notifycenter.utils.template.model.Criteria;

import java.util.List;

/**
 * @author yangkai
 * @date 2019-03-27
 * @email yangkai@hujiang.com
 * @description
 */
public class QnAppCriteria extends Criteria<Integer> {
    private String name;

    public QnAppCriteria(List<Integer> ids) {
        super(ids);
    }

    public QnAppCriteria(String name) {
        this.name = name;
    }
}
