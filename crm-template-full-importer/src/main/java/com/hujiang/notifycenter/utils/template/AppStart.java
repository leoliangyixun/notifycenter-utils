package com.hujiang.notifycenter.utils.template;

import com.hujiang.basic.framework.core.context.SpringApplicationContext;
import com.hujiang.basic.framework.rest.config.service.HJundertow;
import com.hujiang.basic.framework.rest.main.HJApplication;
import com.hujiang.notifycenter.utils.template.service.TemplateService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextStartedEvent;

@Slf4j
@SpringBootApplication(scanBasePackages="com.hujiang.notifycenter.utils.template")
@HJundertow
public class AppStart implements ApplicationListener<ContextStartedEvent> {

    @Autowired
    private TemplateService templateService;

    public static void main(String[] args) {
        HJApplication.start(AppStart.class, args);
        ((ConfigurableApplicationContext) SpringApplicationContext.getApplicationContext()).start();
    }

    @Override
    public void onApplicationEvent(ContextStartedEvent event) {
        templateService.importCrm();
    }
}
