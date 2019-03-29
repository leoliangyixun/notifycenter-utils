package com.hujiang.notifycenter.utils.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextStartedEvent;

import com.hujiang.basic.framework.core.context.SpringApplicationContext;
import com.hujiang.basic.framework.rest.config.service.HJundertow;
import com.hujiang.basic.framework.rest.main.HJApplication;
import com.hujiang.notifycenter.utils.template.service.TemplateService;

@SpringBootApplication(scanBasePackages="com.hujiang.notifycenter.utils.template")
@HJundertow
public class DemoStart implements ApplicationListener<ContextStartedEvent> {
    @Value("${crm.templateIds}")
    private String source;

    @Autowired
    private TemplateService templateService;

    public static void main(String[] args) {
    	HJApplication.start(DemoStart.class, args);
        ((ConfigurableApplicationContext) SpringApplicationContext.getApplicationContext()).start();
    }

    @Override
    public void onApplicationEvent(ContextStartedEvent event) {
        templateService.importCrm(source);
    }
}
