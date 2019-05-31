package com.hujiang.notifycenter.utils.template.core.model.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yangkai
 * @date 2019-05-29
 * @email yangkai@hujiang.com
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CrmTemplateGroupPo extends BasePo {

    private String title;
    private String appName;
    private Integer level;

    // 模版Id
    private Integer templateId;
    // 模版内容Id
    private Integer templateContentId;
    // 发送顺序
    private Integer sortBy;
    // 是否必发
    private Boolean isNecessary;
    private String appId;

    private String content;
    // 模版类型--1/AppPush；2/微信；3/QQ；4/短信；5/Mail；6/TTS;
    private Integer type;

}
