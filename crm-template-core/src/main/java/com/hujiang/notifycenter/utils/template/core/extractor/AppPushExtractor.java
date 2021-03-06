package com.hujiang.notifycenter.utils.template.core.extractor;

import com.hujiang.basic.framework.core.util.JsonUtil;
import com.hujiang.notifycenter.qingniao.model.dto.AppPushMessageDto.Notification.Android;
import com.hujiang.notifycenter.qingniao.model.dto.AppPushMessageDto.Notification.Ios;
import com.hujiang.notifycenter.qingniao.model.dto.AppPushMessageDto.Options;
import com.hujiang.notifycenter.qingniao.model.dto.ContentDto.AppPushContentDto;
import com.hujiang.notifycenter.template.common.model.external.PushBody;
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
public class AppPushExtractor {

    public void extract(CrmTemplateContentDto crm, QnTemplateContentPo qn) {
        try {
            PushBody body = JsonUtil.json2Object(crm.getContent().getContent(), PushBody.class);

            AppPushContentDto apppush = new AppPushContentDto();
            apppush.setAppName(body.getAppname());

            Android android = new Android();
            if (body.getMessage() != null) {
                android.setTitle(body.getMessage().getTitle());
                android.setAlert(body.getMessage().getContent());
                android.setAction(body.getMessage().getAction());
                android.setIsJpushNotification(false);
                android.setMode(body.getMessage().getMode());
                android.setTrack(body.getMessage().getTrack());
                android.setFlags(body.getMessage().getFlags());
                apppush.setAndroid(android);
            }
            if (body.getNotification() != null) {
                Ios ios = new Ios();
                ios.setTitle(null);
                ios.setAlert(body.getNotification().getAlert());
                ios.setBadge(body.getNotification().getBadge());
                ios.setAction(body.getNotification().getAction());
                ios.setMode(body.getNotification().getMode());
                ios.setSound(body.getNotification().getSound());
                ios.setTrack(body.getNotification().getTrack());
                apppush.setIos(ios);
            }

            if (body.getOptions() != null) {
                Options option = new Options();
                option.setTimeToLive((long) body.getOptions().getTime_to_live());
                option.setOverrideMsgId((long) body.getOptions().getOverride_msg_id());
                option.setApnsCollapseId(null);
                apppush.setOptions(option);
            }

            qn.setContent(JsonUtil.object2JSON(apppush));
        } catch (Exception ex) {
            log.error("error", ex);
            log.error("应用推送模板解析异常, 模板: {}", crm.getContent());
        }
    }
}
