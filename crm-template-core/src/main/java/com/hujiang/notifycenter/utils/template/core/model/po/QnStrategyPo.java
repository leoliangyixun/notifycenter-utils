package com.hujiang.notifycenter.utils.template.core.model.po;

import com.hujiang.basic.framework.core.util.JsonUtil;
import com.hujiang.notifycenter.common.consts.MsgLevel;

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
public class QnStrategyPo extends BasePo {
    private String appKey;
    private String title;
    private MsgLevel msgLevel;
    private String description;
    private Integer status;

    @Override
    public String toString() {
        return JsonUtil.object2JSON(this);
    }
}
