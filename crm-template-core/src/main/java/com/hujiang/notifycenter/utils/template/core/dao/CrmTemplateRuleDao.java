package com.hujiang.notifycenter.utils.template.core.dao;

import com.hujiang.basic.framework.dao.annotation.MyBatisRepository;
import com.hujiang.notifycenter.utils.template.core.model.Criteria;
import com.hujiang.notifycenter.utils.template.core.model.Query;

import com.hujiang.notifycenter.utils.template.core.model.po.CrmTemplateRulePo;

import java.util.List;

/**
 * @author yangkai
 * @date 2019-03-20
 * @email yangkai@hujiang.com
 * @description
 */
@MyBatisRepository
public interface CrmTemplateRuleDao {
    <T extends Criteria<T>> List<CrmTemplateRulePo> query(Query<Criteria<T>> query);
}
