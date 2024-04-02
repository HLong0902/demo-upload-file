package com.sudin.configuration;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Component
@Aspect
public class CheckPermission {

    @Value("#{'${whiteList.IP}'.split(',')}")
    private List<String> whiteList;

    @Autowired
    private Map<String, String> loginData;

    @Around("@annotation(com.sudin.configuration.Permission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {


        boolean result = false;
        String ip = "";
        Boolean checkAuthen = true;
        for (Object o : joinPoint.getArgs()) {
            if (o instanceof HttpServletRequest) {
                HttpServletRequest request = (HttpServletRequest) o;
                ip = request.getRemoteAddr();
                if (request.getServletPath().contains("login")) {
                    checkAuthen = false;
                }
            }
        }

        if (checkAuthen) {
            if (whiteList.contains(ip) && loginData.containsKey(ip)) {
                result = true;
            }
        } else {
            if (whiteList.contains(ip)) {
                result = true;
            }
        }

        if (!result) {
            System.out.println("Login fail");
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("login");
            return modelAndView;
        } else {
            System.out.println("Login success by" + ip);
            return joinPoint.proceed();
        }
    }

}
