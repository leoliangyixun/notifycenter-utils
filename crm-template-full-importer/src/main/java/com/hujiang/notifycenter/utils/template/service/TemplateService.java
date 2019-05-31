package com.hujiang.notifycenter.utils.template.service;

import com.hujiang.basic.framework.core.util.JsonUtil;
import com.hujiang.notifycenter.utils.template.core.model.dto.CrmMsgTemplateDto;
import com.hujiang.notifycenter.utils.template.core.resolver.QnTemplateExporter;
import com.hujiang.notifycenter.utils.template.core.resolver.QnTemplateImporter2;
import com.hujiang.notifycenter.utils.template.core.resolver.CrmTemplateExporter;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yangkai
 * @date 2019-05-31
 * @email yangkai@hujiang.com
 * @description
 */
@Slf4j
@Service
public class TemplateService {

    @Value("${qingniao.copy.appKey}")
    private String sourceAppKey;//格式:xxx:yyy,zzz,xxx; 表示将yyy,zzz,xxx3个应用的策略复制给应用xxx

    @Autowired
    private CrmTemplateExporter crmTemplateExporter;

    @Autowired
    private QnTemplateImporter2 qnTemplateImporter;

    @Autowired
    private QnTemplateExporter qnTemplateExporter;

    public void importCrm() {
        List<CrmMsgTemplateDto> list = crmTemplateExporter.exportAll();
        log.info("crm template full export data: {}", JsonUtil.object2JSON(list));
        qnTemplateImporter.resolve(list);
        if (StringUtils.isNotBlank(sourceAppKey)) {
            String[] appKeys = StringUtils.split(sourceAppKey, ":");
            String targetAppKey = appKeys[0];
            String[] sourceAppKeys = StringUtils.split(appKeys[1], ",");
            //qnTemplateExporter.copyByAppKey(targetAppKey, sourceAppKeys);
        }
    }

}
