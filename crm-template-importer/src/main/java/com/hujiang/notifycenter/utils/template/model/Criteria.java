package com.hujiang.notifycenter.utils.template.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author yangkai
 * @date 2019-03-25
 * @email yangkai@hujiang.com
 * @description
 */
@Data
@NoArgsConstructor
public class Criteria<U extends Serializable> {
    private List<U> ids;

    public Criteria(List<U> ids) {
        this.ids = ids;
    }
}
