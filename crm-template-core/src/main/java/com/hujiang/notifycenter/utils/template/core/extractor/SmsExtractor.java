package com.hujiang.notifycenter.utils.template.core.extractor;

import com.hujiang.basic.framework.core.util.JsonUtil;
import com.hujiang.notifycenter.common.consts.MsgLevel;
import com.hujiang.notifycenter.qingniao.model.dto.ContentDto.SmsContentDto;
import com.hujiang.notifycenter.utils.template.core.model.po.QnTemplateContentPo;
import com.hujiang.notifycenter.utils.template.core.model.dto.CrmTemplateContentDto;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

/**
 * @author yangkai
 * @date 2019-05-23
 * @email yangkai@hujiang.com
 * @description
 */
@Slf4j
public class SmsExtractor {

    public void extract(MsgLevel msgLevel, CrmTemplateContentDto crm, QnTemplateContentPo qn) {
        try {
            SmsContentDto sms = new SmsContentDto();
            //短信签名写死
            sms.setSign(msgLevel == MsgLevel.vcode ? "【沪江】" : "【沪江网校】");
            String text = crm.getContent().getContent();
            text = StringUtils.replace(text, "【", "(");
            text = StringUtils.replace(text, "】", ")");
            //{0} -> {{{0}}}
            text = StringUtils.replacePattern(text, "\\{(?!\\{)[^}]+\\}(?!\\})", "{{$0}}");
            sms.setContent(text);
            sms.setIsOversea(false);
            qn.setContent(JsonUtil.object2JSON(sms));
        } catch (Exception ex) {
            log.error("error", ex);
            log.error("短信模板解析异常, 模板: {}", crm.getContent());
        }
    }
}
