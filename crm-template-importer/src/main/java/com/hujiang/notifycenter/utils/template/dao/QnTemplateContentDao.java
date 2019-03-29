package com.hujiang.notifycenter.utils.template.dao;

import com.hujiang.basic.framework.dao.annotation.MyBatisRepository;
import com.hujiang.notifycenter.utils.template.model.po.QnTemplateContentPo;

import java.util.List;

/**
 * @author yangkai
 * @date 2019-03-20
 * @email yangkai@hujiang.com
 * @description
 */
@MyBatisRepository
public interface QnTemplateContentDao {
    int insert(QnTemplateContentPo object);

    int insertBatch(List<QnTemplateContentPo> objects);
}
