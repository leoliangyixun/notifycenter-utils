package com.hujiang.notifycenter.utils.template.core.dao;

import com.hujiang.basic.framework.dao.annotation.MyBatisRepository;
import com.hujiang.notifycenter.utils.template.core.model.Criteria;
import com.hujiang.notifycenter.utils.template.core.model.Query;
import com.hujiang.notifycenter.utils.template.core.model.po.QnStrategyPo;
import com.hujiang.notifycenter.utils.template.core.model.po.QnTemplateContentPo;

import java.util.List;

/**
 * @author yangkai
 * @date 2019-03-26
 * @email yangkai@hujiang.com
 * @description
 */
@MyBatisRepository
public interface QnStrategyDao {
    int insert(QnStrategyPo po);
    int insertBatch(List<QnStrategyPo> list);
    <T extends Criteria<T>> List<QnStrategyPo> query(Query<Criteria<T>> query);
    <T extends Criteria<T>> List<QnStrategyPo> queryAll();
}
