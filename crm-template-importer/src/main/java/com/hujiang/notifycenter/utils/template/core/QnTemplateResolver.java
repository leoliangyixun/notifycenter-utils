package com.hujiang.notifycenter.utils.template.core;

import com.hujiang.notifycenter.qingniao.model.dto.TemplateListDto;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class QnTemplateResolver extends TemplateResolver {

    @Override
    public void execute(List<Integer> templateIds, List<TemplateListDto> optionalTemplates, List<TemplateListDto> requiredTemplates) {
        if (CollectionUtils.isNotEmpty(templateIds) && CollectionUtils.isNotEmpty(optionalTemplates)) {
            //有必发, 有补发
            //由于老的模板消息必发与补发是隔离的，先发所有的必发，必发都失败的用户再根据优先级补发
            //消息中心2.0为了尽可能兼容老的模板消息,将补发模板作为最后一个必发模板对应的补发模板
            for (int i = 0 ;i < templateIds.size(); i++) {
                TemplateListDto template = new TemplateListDto();
                template.setTemplateId(templateIds.get(i));
                if (i == templateIds.size() - 1) {
                    template.setOptionalTemplates(optionalTemplates);
                }
                requiredTemplates.add(template);
            }
        } else {
            //必发
            if (CollectionUtils.isNotEmpty(templateIds)) {
                for (Integer templateId : templateIds) {
                    TemplateListDto template = new TemplateListDto();
                    template.setTemplateId(templateId);
                    requiredTemplates.add(template);
                }
            }
            //补发
            if (CollectionUtils.isNotEmpty(optionalTemplates)) {
                TemplateListDto template = new TemplateListDto();
                template.setOptionalTemplates(optionalTemplates);
                requiredTemplates.add(template);
            }
        }
    }

}
