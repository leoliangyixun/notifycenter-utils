package com.hujiang.notifycenter.utils.template.core.model.criteria;

import com.hujiang.notifycenter.utils.template.core.model.Criteria;

import lombok.Builder;
import lombok.Getter;

/**
 * @author yangkai
 * @date 2019-03-27
 * @email yangkai@hujiang.com
 * @description
 */
public class QnAppCriteria implements Criteria<QnAppCriteria> {

    @Getter
    private String name;

    public QnAppCriteria name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public QnAppCriteria build() {
        return this;
    }
}
