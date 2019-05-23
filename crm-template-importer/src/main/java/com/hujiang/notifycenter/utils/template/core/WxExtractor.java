package com.hujiang.notifycenter.utils.template.core;

import com.hujiang.basic.framework.core.util.JsonUtil;
import com.hujiang.notifycenter.qingniao.constants.WxSubMsgType;
import com.hujiang.notifycenter.qingniao.model.dto.ContentDto.WxContentDto;
import com.hujiang.notifycenter.qingniao.model.dto.ContentDto.WxContentDto.WxMessageContentDto;
import com.hujiang.notifycenter.template.common.model.external.WechatTemplateBody;
import com.hujiang.notifycenter.template.common.model.external.WechatTemplateBodyV2;
import com.hujiang.notifycenter.utils.template.model.dto.CrmTemplateDto;
import com.hujiang.notifycenter.utils.template.model.po.QnTemplateContentPo;
import com.hujiang.notifycenter.wechat.support.model.dto.WXMessage2.MiniProgram;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author yangkai
 * @date 2019-05-23
 * @email yangkai@hujiang.com
 * @description
 */
@Slf4j
public class WxExtractor {

    private CrmTemplateDto crm;
    private QnTemplateContentPo content;

    public WxExtractor(CrmTemplateDto crm, QnTemplateContentPo content) {
        this.crm = crm;
        this.content = content;
    }

    public void extract() {
        boolean failed;
        try {
            Map<String, Object> result = JsonUtil.json2Map(crm.getContent().getContent());
            if (result.containsKey("version") && result.get("version").equals("v2")) {
                WechatTemplateBodyV2 body = JsonUtil
                    .json2Object(crm.getContent().getContent(), WechatTemplateBodyV2.class);
                WxMessageContentDto template = new WxMessageContentDto();
                template.setTemplateId(body.getTemplateId());
                template.setUrl(body.getUrl());
                if (body.getMiniprogram() != null) {
                    MiniProgram miniprogram = new MiniProgram();
                    miniprogram.setAppid(body.getMiniprogram().getAppid());
                    miniprogram.setPagepath(body.getMiniprogram().getPagepath());
                    template.setMiniprogram(miniprogram);
                }

                WxContentDto wx = new WxContentDto();
                wx.setWechatAppId(body.getWechatAppId());
                wx.setWxMsgType(WxSubMsgType.tmpl);
                wx.setTemplate(template);

                content.setContent(wx.toString());
            } else {
                WechatTemplateBody body = JsonUtil
                    .json2Object(crm.getContent().getContent(), WechatTemplateBody.class);
                WxMessageContentDto template = new WxMessageContentDto();
                template.setTemplateId(body.getTemplate_id());
                template.setUrl(body.getUrl());
                if (body.getMiniprogram() != null) {
                    MiniProgram miniprogram = new MiniProgram();
                    miniprogram.setAppid(body.getMiniprogram().getAppid());
                    miniprogram.setPagepath(body.getMiniprogram().getPagepath());
                    template.setMiniprogram(miniprogram);
                }

                WxContentDto wx = new WxContentDto();
                wx.setWechatAppId(body.getWechatAppId());
                wx.setWxMsgType(WxSubMsgType.tmpl);
                wx.setTemplate(template);

                content.setContent(wx.toString());
            }
        } catch (Exception ex) {
            failed = true;
            log.error("error", ex);
            log.error("微信模板解析异常, 模板: {}", crm.getContent());
        }
    }
}
