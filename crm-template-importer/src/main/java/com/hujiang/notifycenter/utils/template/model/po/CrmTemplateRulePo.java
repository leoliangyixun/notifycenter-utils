package com.hujiang.notifycenter.utils.template.model.po;

import com.hujiang.basic.framework.core.util.JsonUtil;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yangkai
 * @date 2019-03-25
 * @email yangkai@hujiang.com
 * @description
 */
@Data
public class CrmTemplateRulePo implements Serializable {
    // 模版Id
    private Integer templateId;
    // 模版内容Id
    private Integer templateContentId;
    // 发送顺序
    private Integer sortBy;
    // 是否必发
    private Boolean isNecessary;

    @Override
    public String toString() {
        return JsonUtil.object2JSON(this);
    }
}
