package com.hujiang.notifycenter.utils.template.core.model.dto;

import com.hujiang.basic.framework.core.util.JsonUtil;
import com.hujiang.notifycenter.utils.template.core.model.po.CrmTemplateContentPo;
import com.hujiang.notifycenter.utils.template.core.model.po.CrmTemplateRulePo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangkai
 * @date 2019-03-25
 * @email yangkai@hujiang.com
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrmTemplateContentDto {
    private CrmTemplateRulePo rule;
    private CrmTemplateContentPo content;

    @Override
    public String toString() {
        return JsonUtil.object2JSON(this);
    }

}
