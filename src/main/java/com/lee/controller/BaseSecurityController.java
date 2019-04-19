package com.lee.controller;

import com.lee.properties.MySecurityProperties;
import com.lee.enums.ExceptionEnum;
import com.lee.vo.HttpResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
public class BaseSecurityController {

    @Autowired
    private MySecurityProperties securityProperties;

    /**
     * 若配置了authenticationEntryPoint 会首先进入authenticationEntryPoint处理
     * 对应SecurityConfig配置的loginPage，需要登录认证时由security跳转到此处
     */
    @RequestMapping("/authentication/require")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)   //返回403未认证状态码
    public HttpResponseResult requireAuthentication(){
        // 返回json数据
        return HttpResponseResult.error(ExceptionEnum.UNAUTHORIZED);
    }


    /**
     * session跳转的url
     */
    @RequestMapping("/session/invalid")
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public HttpResponseResult sessionInvalid(){
        return new HttpResponseResult(401,"session已过期请重新登录");
    }

}
