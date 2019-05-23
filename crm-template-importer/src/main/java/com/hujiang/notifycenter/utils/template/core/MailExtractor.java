package com.hujiang.notifycenter.utils.template.core;

import com.hujiang.basic.framework.core.util.JsonUtil;
import com.hujiang.notifycenter.qingniao.model.dto.ContentDto.MailContentDto;
import com.hujiang.notifycenter.template.common.model.external.EmailBody;
import com.hujiang.notifycenter.utils.template.model.dto.CrmTemplateDto;
import com.hujiang.notifycenter.utils.template.model.po.QnTemplateContentPo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yangkai
 * @date 2019-05-23
 * @email yangkai@hujiang.com
 * @description
 */
@Slf4j
public class MailExtractor {

    private CrmTemplateDto crm;
    private QnTemplateContentPo qn;

    public MailExtractor(CrmTemplateDto crm, QnTemplateContentPo qn) {
        this.crm = crm;
        this.qn = qn;
    }

    public void extract() {
        boolean failed;
        try {
            EmailBody body = JsonUtil.json2Object(crm.getContent().getContent(), EmailBody.class);
            MailContentDto mail = new MailContentDto();
            mail.setIsInternal(false);
            mail.setSubject(body.getSubject());
            mail.setTemplet(body.getTemplet());

            qn.setContent(mail.toString());
        } catch (Exception ex) {
            failed = true;
            log.error("error", ex);
            log.error("邮件模板解析异常, 模板: {}", crm.getContent());
        }
    }
}
