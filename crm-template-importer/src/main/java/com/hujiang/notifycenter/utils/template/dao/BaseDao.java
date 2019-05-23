package com.hujiang.notifycenter.utils.template.dao;

import com.hujiang.notifycenter.utils.template.model.Criteria;
import com.hujiang.notifycenter.utils.template.model.Query;
import com.hujiang.notifycenter.utils.template.model.po.BasePo;

import java.io.Serializable;
import java.util.List;

/**
 * @author yangkai
 * @date 2019-05-19
 * @email yangkai@hujiang.com
 * @description
 */
public interface BaseDao {
    <ID extends Serializable, U extends BasePo> List<U> query(Query<Criteria<ID>> query);
}
