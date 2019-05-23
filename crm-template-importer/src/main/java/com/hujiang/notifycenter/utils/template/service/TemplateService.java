package com.hujiang.notifycenter.utils.template.service;

import com.hujiang.notifycenter.template.common.Helper;
import com.hujiang.notifycenter.utils.template.core.CrmTemplateExtractor;
import com.hujiang.notifycenter.utils.template.core.QnTemplateResolver2;
import com.hujiang.notifycenter.utils.template.model.dto.CrmTemplateGroupDto;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 将消息中心1.0老的对接CRM的模板消息系统的模板导入消息中心2.0青鸟系统
 *
 * CRM               2.0(qingniao)
 * MsgTemplatePo     StrategyPo
 * TemplateRulePo    TemplateGroupPo
 * TemplateContentPo TemplateContentPo
 * @author yangkai
 * @date 2019-03-25
 * @email yangkai@hujiang.com
 * @description
 */
@Service
public class TemplateService {

    @Autowired
    private CrmTemplateExtractor crmTemplateExtractor;

/*    @Autowired
    private QnTemplateResolver qnTemplateResolver;*/

    @Autowired
    private QnTemplateResolver2 qnTemplateResolver;

    public void importCrm(String source) {
        if (StringUtils.isNotBlank(source)) {
            List<Integer> ids = Stream.of(StringUtils.split(source, ",")).map(e -> {
                try {
                    return Integer.valueOf(Helper.decrypt(e));
                } catch (InvalidKeyException e1) {
                    e1.printStackTrace();
                } catch (InvalidKeySpecException e1) {
                    e1.printStackTrace();
                } catch (NoSuchAlgorithmException e1) {
                    e1.printStackTrace();
                } catch (NoSuchPaddingException e1) {
                    e1.printStackTrace();
                } catch (InvalidAlgorithmParameterException e1) {
                    e1.printStackTrace();
                } catch (IllegalBlockSizeException e1) {
                    e1.printStackTrace();
                } catch (BadPaddingException e1) {
                    e1.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());

            List<CrmTemplateGroupDto> list = crmTemplateExtractor.extract(ids);
            System.out.println(list);
            qnTemplateResolver.resolve(list);
        }
    }

}
