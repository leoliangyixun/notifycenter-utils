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

@EqualsAndHashCode(callSuper = true)
public class CrmMsgTemplatePo extends BasePo {
    private String title;
    private String appName;
    private Integer level;

    public CrmMsgTemplatePo(Integer id, String title, String appName, Integer level) {
        this.id = id;
        this.title = title;
        this.appName = appName;
        this.level = level;
    }

    @Override
    public String toString() {
        return JsonUtil.object2JSON(this);
    }
}
