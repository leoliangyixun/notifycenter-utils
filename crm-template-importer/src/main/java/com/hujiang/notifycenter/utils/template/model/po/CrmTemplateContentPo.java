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
public class CrmTemplateContentPo implements Serializable {
    private Integer id;
    private String content;
    // 模版类型--1/AppPush；2/微信；3/QQ；4/短信；5/Mail；6/TTS;
    private Integer type;

    @Override
    public String toString() {
        return JsonUtil.object2JSON(this);
    }
}
