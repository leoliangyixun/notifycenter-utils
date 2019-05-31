package com.hujiang.notifycenter.utils.template.core.extractor;

import com.hujiang.basic.framework.core.util.JsonUtil;
import com.hujiang.notifycenter.qingniao.model.dto.ContentDto.MailContentDto;
import com.hujiang.notifycenter.template.common.model.external.EmailBody;
import com.hujiang.notifycenter.utils.template.core.model.po.QnTemplateContentPo;
import com.hujiang.notifycenter.utils.template.core.model.dto.CrmTemplateContentDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yangkai
 * @date 2019-05-23
 * @email yangkai@hujiang.com
 * @description
 */
@Slf4j
public class MailExtractor {

    public void extract(CrmTemplateContentDto crm, QnTemplateContentPo qn) {
        try {
            EmailBody body = JsonUtil.json2Object(crm.getContent().getContent(), EmailBody.class);
            MailContentDto mail = new MailContentDto();
            mail.setIsInternal(false);
            mail.setSubject(body.getSubject());
            mail.setTemplet(body.getTemplet());

            qn.setContent(JsonUtil.object2JSON(mail));
        } catch (Exception ex) {
            log.error("error", ex);
            log.error("邮件模板解析异常, 模板: {}", crm.getContent());
        }
    }
}
