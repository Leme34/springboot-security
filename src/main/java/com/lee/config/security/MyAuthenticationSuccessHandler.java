package com.lee.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.controller.ValidateCodeController;
import com.lee.vo.HttpResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * security认证成功后调用此类的onAuthenticationSuccess方法
 */
@Slf4j
@Component
public class MyAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Lazy //延迟加载，当需要用的时候才注入，解决与SecurityConfig循环依赖问题
    @Autowired
    private SessionStrategy sessionStrategy;

    /**
     * security登录成功时会被调用
     * authentication: 登录后的用户信息对象
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        String username = authentication.getName();
        log.info(username + "登录成功");

        // 清除session中此次登录的验证码
        sessionStrategy.removeAttribute(new ServletWebRequest(request), ValidateCodeController.SESSION_KEY);

        //返回用户信息对象principal给小程序
        response.setContentType("application/json;charset=UTF-8");
        ResponseEntity<HttpResponseResult> responseEntity = ResponseEntity.ok(new HttpResponseResult(200, authentication.getPrincipal()));
        response.getWriter().write(objectMapper.writeValueAsString(responseEntity));
    }
}
