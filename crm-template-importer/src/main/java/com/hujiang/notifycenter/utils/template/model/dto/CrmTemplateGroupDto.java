package com.hujiang.notifycenter.utils.template.model.dto;

import com.hujiang.basic.framework.core.util.JsonUtil;
import com.hujiang.notifycenter.utils.template.model.po.CrmMsgTemplatePo;

import lombok.Data;

import java.util.List;

/**
 * 对应消息中心2.0的组合推送
 * @author yangkai
 * @date 2019-03-25
 * @email yangkai@hujiang.com
 * @description
 */
@Data
public class CrmTemplateGroupDto {
    //CRM模版Id
    private String templateId;
    //CRM模板信息
    private CrmMsgTemplatePo template;
    //CRM模板内容信息(只有一个必发模板)
    private List<CrmTemplateDto> contents;

    @Override
    public String toString() {
        return JsonUtil.object2JSON(this);
    }
}
