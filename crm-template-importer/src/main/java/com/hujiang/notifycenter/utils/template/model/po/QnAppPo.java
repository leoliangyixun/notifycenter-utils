
package com.hujiang.notifycenter.utils.template.model.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class QnAppPo extends BasePo {
    private String name;
    private String appKey;
    private String appSecret;

}