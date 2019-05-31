
package com.hujiang.notifycenter.utils.template.core.model.po;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QnAppPo extends BasePo {
    private String name;
    private String appKey;
    private String appSecret;

}