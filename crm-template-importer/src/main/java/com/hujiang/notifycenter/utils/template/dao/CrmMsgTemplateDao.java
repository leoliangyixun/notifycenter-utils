package com.hujiang.notifycenter.utils.template.dao;

import com.hujiang.basic.framework.dao.annotation.MyBatisRepository;
import com.hujiang.notifycenter.utils.template.model.Criteria;
import com.hujiang.notifycenter.utils.template.model.Query;
import com.hujiang.notifycenter.utils.template.model.po.CrmMsgTemplatePo;

import java.io.Serializable;
import java.util.List;

/**
 * @author yangkai
 * @date 2019-03-20
 * @email yangkai@hujiang.com
 * @description
 */
@MyBatisRepository
public interface CrmMsgTemplateDao {
    <U extends Serializable> List<CrmMsgTemplatePo> query(Query<Criteria<U>> query);

}
