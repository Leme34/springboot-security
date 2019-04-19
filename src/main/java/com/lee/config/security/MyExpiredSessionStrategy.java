package com.lee.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.vo.HttpResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class MyExpiredSessionStrategy implements SessionInformationExpiredStrategy {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 同一用户在别处登录使旧的session替换为新的session时的操作
     * 可以使用response.sendRedirect(url)跳转页面
     * 此处使用response的writer返回json数据通知小程序用户被挤下线,删除过期的cookie再重新登录
     *
     * @param event 旧的session过期事件,对象里有新旧session替换前后的request、response
     */
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        log.info("MyExpiredSessionStrategy: session过期事件,用户在别处登录,已覆盖此用户之前登录的session");
        HttpServletResponse response = event.getResponse();
        response.setContentType("application/json;charset=UTF-8");
        ResponseEntity<HttpResponseResult> responseEntity = ResponseEntity
                .ok(new HttpResponseResult(201, "您的账号在别处登录,请重新登录~"));
        response.getWriter().write(objectMapper.writeValueAsString(responseEntity));
    }
}
