package com.hujiang.notifycenter.utils.template.core;

import com.hujiang.notifycenter.common.consts.MsgLevel;
import com.hujiang.notifycenter.qingniao.model.dto.ContentDto.SmsContentDto;
import com.hujiang.notifycenter.utils.template.model.dto.CrmTemplateDto;
import com.hujiang.notifycenter.utils.template.model.po.QnTemplateContentPo;

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

    private MsgLevel msgLevel;
    private CrmTemplateDto crm;
    private QnTemplateContentPo content;

    public SmsExtractor(MsgLevel msgLevel, CrmTemplateDto crm, QnTemplateContentPo content) {
        this.msgLevel = msgLevel;
        this.crm = crm;
        this.content = content;
    }

    public void extract() {
        boolean failed;
        try {
            SmsContentDto sms = new SmsContentDto();
            //TODO 短信签名写死
            sms.setSign(msgLevel == MsgLevel.vcode ? "【沪江】" : "【沪江网校】");
            String text = crm.getContent().getContent();
            text = StringUtils.replace(text, "【", "(");
            text = StringUtils.replace(text, "】", ")");
            sms.setContent(text);
            sms.setIsOversea(false);
            content.setContent(sms.toString());
        } catch (Exception ex) {
            failed = true;
            log.error("error", ex);
            log.error("短信模板解析异常, 模板: {}", crm.getContent());
        }
    }
}
