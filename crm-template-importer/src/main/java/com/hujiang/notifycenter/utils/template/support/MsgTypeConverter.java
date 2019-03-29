package com.hujiang.notifycenter.utils.template.support;

import com.hujiang.notifycenter.qingniao.constants.MsgType;

/**
 * @author yangkai
 * @date 2019-03-27
 * @email yangkai@hujiang.com
 * @description
 */
public class MsgTypeConverter {

    /**
     *  // 模版类型--1/AppPush；2/微信； 3/QQ；4/短信；5/Mail；
     * @param type
     * @return
     */
    public static MsgType getMsgType(int type) {
        if (type == 1) {
            return MsgType.apppush;
        }  else if (type == 2) {
            return MsgType.wechat;
        }  else if (type == 4) {
            return MsgType.sms;
        }  else if (type == 5) {
            return MsgType.mail;
        } else {
            return null;
        }
    }

}
