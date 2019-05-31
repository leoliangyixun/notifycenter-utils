package com.hujiang.notifycenter.utils.template.core.model;

import java.io.Serializable;

/**
 * @author yangkai
 * @date 2019-03-25
 * @email yangkai@hujiang.com
 * @description
 */
public interface Criteria<U extends Criteria<?>> extends Serializable {
    U build();
}
