package com.sudin.configuration;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class CustomApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Get the ApplicationEventMulticaster bean and publish your custom events here
        // Example:
         ApplicationEventMulticaster eventMulticaster = event.getApplicationContext().getBean(ApplicationEventMulticaster.class);
    }
}