package com.hujiang.notifycenter.utils.template.service;

import com.hujiang.basic.framework.core.util.JsonUtil;
import com.hujiang.notifycenter.template.common.Helper;
import com.hujiang.notifycenter.utils.template.core.model.dto.CrmMsgTemplateDto;
import com.hujiang.notifycenter.utils.template.core.resolver.CrmTemplateExporter;
import com.hujiang.notifycenter.utils.template.core.resolver.QnTemplateImporter2;
import com.hujiang.notifycenter.utils.template.core.resolver.TemplateImporter;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * @author yangkai
 * @date 2019-05-31
 * @email yangkai@hujiang.com
 * @description
 */
@Slf4j
@Service
public class TemplateService {

    @Value("${crm.templateIds}")
    private String source;

    @Autowired
    private CrmTemplateExporter crmTemplateExporter;

    @Autowired
    private QnTemplateImporter2 qnTemplateImporter;

    public void importCrm() {
        if (StringUtils.isNotBlank(source)) {
            List<Integer> ids = Stream.of(StringUtils.split(source, ",")).map(e -> {
                try {
                    return Integer.valueOf(Helper.decrypt(e));
                } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
                    ex.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());

            List<CrmMsgTemplateDto> list = crmTemplateExporter.export(ids);
            log.info("crm template export data: {}", JsonUtil.object2JSON(list));
            qnTemplateImporter.resolve(list);
        }
    }

}
