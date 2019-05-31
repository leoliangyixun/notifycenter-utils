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
public class CrmTemplateContentPo extends BasePo {
    private String content;
    // 模版类型--1/AppPush；2/微信；3/QQ；4/短信；5/Mail；6/TTS;
    private Integer type;

    public CrmTemplateContentPo(Integer id, String content, Integer type) {
        this.id = id;
        this.content = content;
        this.type = type;
    }

    @Override
    public String toString() {
        return JsonUtil.object2JSON(this);
    }
}
