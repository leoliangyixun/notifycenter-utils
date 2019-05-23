package com.hujiang.notifycenter.utils.template.core;

import com.hujiang.notifycenter.qingniao.model.dto.TemplateListDto;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class QnTemplateResolver2 extends TemplateResolver {

    /**
     * 不允许没有必发, 没有必发时将优先级最高的补发作为必发, 剩下的补发作为该必发的补发
     * @param requiredTemplateIds
     * @param optionalTemplates
     * @param requiredTemplates
     */
    @Override
    public void execute(List<Integer> requiredTemplateIds, List<TemplateListDto> optionalTemplates, List<TemplateListDto> requiredTemplates) {
        if (CollectionUtils.isNotEmpty(requiredTemplateIds)) {
            //有必发
            //由于老的模板消息必发与补发是隔离的，先发所有的必发，必发都失败的用户再根据优先级补发
            //消息中心2.0为了尽可能兼容老的模板消息,将补发模板作为最后一个必发模板对应的补发模板
            for (int i = 0 ;i < requiredTemplateIds.size(); i++) {
                TemplateListDto template = new TemplateListDto();
                template.setTemplateId(requiredTemplateIds.get(i));
                if (i == requiredTemplateIds.size() - 1) {
                    template.setOptionalTemplates(optionalTemplates);
                }
                requiredTemplates.add(template);
            }
        } else {
            //没有必发，将优先级最高的补发作为必发
            if (CollectionUtils.isNotEmpty(optionalTemplates)) {
                TemplateListDto template = new TemplateListDto();
                template.setTemplateId(optionalTemplates.get(0).getTemplateId());
                if (optionalTemplates.size() > 1) {
                    template.setOptionalTemplates(optionalTemplates.subList(1, optionalTemplates.size()));
                }
                requiredTemplates.add(template);
            }
        }
    }

}
