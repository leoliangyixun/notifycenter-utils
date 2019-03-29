package com.hujiang.notifycenter.utils.template.model.po;

import com.hujiang.basic.framework.core.util.JsonUtil;
import com.hujiang.notifycenter.common.consts.MsgLevel;
import com.hujiang.notifycenter.qingniao.constants.MsgType;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yangkai
 * @date 2019-03-25
 * @email yangkai@hujiang.com
 * @description
 */
@Data
public class QnTemplateContentPo implements Serializable {
    private Integer id;
    private String appKey;
    private MsgLevel msgLevel;
    private String title;
    private MsgType msgType;
    private String content;
    private Integer status;

    @Override
    public String toString() {
        return JsonUtil.object2JSON(this);
    }
}
