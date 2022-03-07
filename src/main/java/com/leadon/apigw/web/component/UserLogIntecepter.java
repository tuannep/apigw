package com.leadon.apigw.web.component;

import com.leadon.apigw.service.impl.UserLogServiceImpl;
import com.leadon.apigw.web.entity.UserLogEntity;
import com.leadon.apigw.web.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class UserLogIntecepter extends HandlerInterceptorAdapter {

    @Autowired
    private UserLogServiceImpl userLogService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {

        if ("GET".equals(request.getMethod())) {
            return;
        }
        if (StringUtils.containsAny(request.getRequestURI() , "get" , "find")) {
            return;
        }
        String username = "unknow";
        try {
            username = SecurityUtils.getUserName();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        UserLogEntity entity = new UserLogEntity();
        entity.setUsername(username);
        entity.setIp(request.getRemoteAddr());
        entity.setServiceName(request.getRequestURI());
        entity.setAction(request.getMethod());

        userLogService.traceLogUser(entity);
    }

}
