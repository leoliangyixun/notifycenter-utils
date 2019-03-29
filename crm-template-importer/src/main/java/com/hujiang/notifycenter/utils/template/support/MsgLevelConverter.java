package com.hujiang.notifycenter.utils.template.support;

import com.hujiang.notifycenter.common.consts.MsgLevel;

/**
 * @author yangkai
 * @date 2019-03-25
 * @email yangkai@hujiang.com
 * @description
 */
public class MsgLevelConverter {

    //A(1),验证码 B(2),通知 C(3);营销
    public static MsgLevel getMsgLevel(int level) {
        if (1 == level) {
            return MsgLevel.vcode;
        } else if (2 == level) {
            return MsgLevel.notice;
        } else if (3 == level) {
            return MsgLevel.ad;
        } else {
            return null;
        }

    }
}
