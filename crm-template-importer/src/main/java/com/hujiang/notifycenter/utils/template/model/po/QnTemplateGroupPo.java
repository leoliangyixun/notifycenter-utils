package com.hujiang.notifycenter.utils.template.model.po;

import com.hujiang.basic.framework.core.util.JsonUtil;
import com.hujiang.notifycenter.qingniao.model.dto.TemplateInfoDto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yangkai
 * @date 2019-03-25
 * @email yangkai@hujiang.com
 * @description
 */
@Data
public class QnTemplateGroupPo implements Serializable {
    private String appKey;
    private Integer strategyId;
    private String title;
    private TemplateInfoDto templateInfo;
    private Integer status;

    @Override
    public String toString() {
        return JsonUtil.object2JSON(this);
    }
}
