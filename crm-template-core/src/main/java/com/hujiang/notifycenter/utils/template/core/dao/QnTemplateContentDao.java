package com.hujiang.notifycenter.utils.template.core.dao;

import com.hujiang.basic.framework.dao.annotation.MyBatisRepository;
import com.hujiang.notifycenter.utils.template.core.model.Criteria;
import com.hujiang.notifycenter.utils.template.core.model.Query;
import com.hujiang.notifycenter.utils.template.core.model.po.QnTemplateContentPo;
import com.hujiang.notifycenter.utils.template.core.model.po.QnTemplateGroupPo;

import java.util.List;

/**
 * @author yangkai
 * @date 2019-03-20
 * @email yangkai@hujiang.com
 * @description
 */
@MyBatisRepository
public interface QnTemplateContentDao {
    int insert(QnTemplateContentPo po);
    int insertBatch(List<QnTemplateContentPo> list);
    <T extends Criteria<T>> List<QnTemplateContentPo> query(Query<Criteria<T>> query);
}
