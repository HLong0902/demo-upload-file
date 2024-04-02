package com.sudin.configuration;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.*;

@Configuration
public class ContextStorage {

    @Bean("loginData")
    public Map<String, String> createLoginDataContext(){
//        <IP, username>
        Map<String, String> loginData = new HashMap<>();
        return loginData;
    }
}
