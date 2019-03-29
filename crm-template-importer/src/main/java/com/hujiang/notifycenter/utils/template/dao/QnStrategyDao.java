package com.hujiang.notifycenter.utils.template.dao;

import com.hujiang.basic.framework.dao.annotation.MyBatisRepository;
import com.hujiang.notifycenter.utils.template.model.po.QnStrategyPo;

/**
 * @author yangkai
 * @date 2019-03-26
 * @email yangkai@hujiang.com
 * @description
 */
@MyBatisRepository
public interface QnStrategyDao {
    int insert(QnStrategyPo object);
}
