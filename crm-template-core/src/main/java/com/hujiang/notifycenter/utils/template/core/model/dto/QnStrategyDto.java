package com.hujiang.notifycenter.utils.template.core.model.dto;

import com.hujiang.basic.framework.core.util.JsonUtil;
import com.hujiang.notifycenter.common.consts.MsgLevel;
import com.hujiang.notifycenter.utils.template.core.model.po.BasePo;
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
public class QnStrategyDto {
    private Integer id;
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
