package com.hujiang.notifycenter.utils.template.model.dto;

import com.hujiang.basic.framework.core.util.JsonUtil;
import com.hujiang.basic.framework.rest.validation.annotation.In;
import com.hujiang.notifycenter.template.common.Helper;
import com.hujiang.notifycenter.utils.template.model.po.CrmMsgTemplatePo;

import lombok.Data;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

/**
 * 对应消息中心2.0的组合推送
 * @author yangkai
 * @date 2019-03-25
 * @email yangkai@hujiang.com
 * @description
 */
@Data
public class CrmTemplateGroupDto {
    //CRM模版Id
    private String templateId;
    //CRM模板信息
    private CrmMsgTemplatePo template;
    //CRM模板内容信息(只有一个必发模板)
    private List<CrmTemplateDto> contents;

    public Integer getIntTemplateId() {
        try {
            return templateId != null ? Integer.valueOf(Helper.decrypt(templateId)) : null;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String toString() {
        return JsonUtil.object2JSON(this);
    }
}
