package com.lee.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.properties.MySecurityProperties;
import com.lee.vo.HttpResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 当匿名用户访问无权限资源时的处理
 * 判断引发跳转的请求类型（html或ajax），若html请求则重定向html登录页，否则返回json数据
 */
@Slf4j
@Component
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint{
    @Autowired
    private MySecurityProperties securityProperties;
    @Autowired
    private ObjectMapper objectMapper;

    // security的请求缓存，security会缓存引发跳转（loginPage）的请求
    private RequestCache requestCache = new HttpSessionRequestCache();
    // security的重定向工具类
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("MyAuthenticationEntryPoint: 匿名用户访问无权限资源被拒绝");

        // 1 从请求缓存取得引发跳转的请求
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        // 存在引发跳转的请求，则跳转登录页
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            log.info("引发security跳转登录页的请求为：" + targetUrl);
            // 2 若是html请求引发的跳转,重定向html登录页
            if (StringUtils.endsWithIgnoreCase(targetUrl, ".html")) {
                redirectStrategy.sendRedirect(request, response,
                        securityProperties.getBrowser().getLoginPage());
            }
        }
        // 否则返回json数据
        ResponseEntity<HttpResponseResult> responseEntity = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new HttpResponseResult(401, "没有权限访问哦"));
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseEntity));
    }

}
