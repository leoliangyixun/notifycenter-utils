package com.hujiang.notifycenter.utils.template.core.dao;

import com.hujiang.basic.framework.dao.model.PageRequest;
import com.hujiang.notifycenter.utils.template.core.model.Criteria;
import com.hujiang.notifycenter.utils.template.core.model.Query;

import com.github.pagehelper.Page;

import com.hujiang.notifycenter.utils.template.core.model.po.BasePo;

import java.util.List;

/**
 * @author yangkai
 * @date 2019-05-19
 * @email yangkai@hujiang.com
 * @description
 */
public interface BaseDao {
    <T extends Criteria<T>, U extends BasePo> List<U> queryByCriteria(Query<Criteria<T>> query);
    <T extends Criteria<T>, U extends BasePo> Page<U> queryByPage(PageRequest<Criteria<T>> request);
}
