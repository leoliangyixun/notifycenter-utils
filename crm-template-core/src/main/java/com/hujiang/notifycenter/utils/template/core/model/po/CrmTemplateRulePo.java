package com.hujiang.notifycenter.utils.template.core.model.po;

import com.hujiang.basic.framework.core.util.JsonUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author yangkai
 * @date 2019-03-25
 * @email yangkai@hujiang.com
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CrmTemplateRulePo extends BasePo {
    // 模版Id
    private Integer templateId;
    // 模版内容Id
    private Integer templateContentId;
    // 发送顺序
    private Integer sortBy;
    // 是否必发
    private Boolean isNecessary;
    private String appId;

    @Override
    public String toString() {
        return JsonUtil.object2JSON(this);
    }
}
