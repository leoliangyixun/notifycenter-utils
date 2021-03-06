package com.hujiang.notifycenter.utils.template.core.model.po;

import com.hujiang.basic.framework.core.util.JsonUtil;
import com.hujiang.notifycenter.qingniao.model.dto.TemplateInfoDto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yangkai
 * @date 2019-03-25
 * @email yangkai@hujiang.com
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QnTemplateGroupPo extends BasePo {
    private String appKey;
    private Integer strategyId;
    private String title;
    private TemplateInfoDto templateInfo;
    private String description;
    private Integer status;

    @Override
    public String toString() {
        return JsonUtil.object2JSON(this);
    }
}
