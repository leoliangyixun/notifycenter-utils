package com.hujiang.notifycenter.utils.template.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author yangkai
 * @date 2019-03-25
 * @email yangkai@hujiang.com
 * @description
 */
@NoArgsConstructor
public class Query<T extends Criteria<? extends Serializable>> implements Serializable {

    public Query(T criteria) {
        this.criteria = criteria;
    }

    @Getter
    @Setter
    private T criteria;
}
