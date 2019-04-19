package com.lee.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.vo.HttpResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * security认证失败时调用此类的onAuthenticationSuccess方法
 */
@Component
public class MyAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * security认证失败时调用
     * @param exception 会根据认证失败原因抛出不同exception
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errMsg = exception.getMessage();
        //根据不同异常翻译错误原因
        if (exception instanceof BadCredentialsException){
            errMsg = "用户名或密码错误";
        }
        //响应错误状态码和错误信息
        ResponseEntity<HttpResponseResult> responseEntity = ResponseEntity.status(HttpStatus.UNAUTHORIZED.value())
                .body(new HttpResponseResult(401, errMsg));
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseEntity));
    }
}
