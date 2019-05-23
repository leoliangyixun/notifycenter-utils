package com.hujiang.notifycenter.utils.template.model.po;

import com.hujiang.basic.framework.core.util.JsonUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author yangkai
 * @date 2019-03-25
 * @email yangkai@hujiang.com
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CrmMsgTemplatePo extends BasePo {
    private String title;
    private String appName;
    private Integer level;

    @Override
    public String toString() {
        return JsonUtil.object2JSON(this);
    }
}
