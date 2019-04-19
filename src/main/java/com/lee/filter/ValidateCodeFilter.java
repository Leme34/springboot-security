package com.lee.filter;

import com.lee.common.SecurityConstants;
import com.lee.controller.ValidateCodeController;
import com.lee.enums.ValidateCodeType;
import com.lee.exception.ValidateCodeException;
import com.lee.validate.ImageCode;
import com.lee.validate.ValidateCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 因为servlet版本不同一次请求被过滤的次数不一定只有1次，所以使用OncePerRequestFilter
 * 确保一次表单登录请求只通过一次filter，而不需要重复执行
 */
@Component
@Slf4j
public class ValidateCodeFilter extends OncePerRequestFilter {

    // 自定义认证失败处理器,用于异常处理
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private SessionStrategy sessionStrategy;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1 若是表单登录 post 请求
        if (StringUtils.equals(SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_FORM, request.getRequestURI()) &&
                StringUtils.equalsIgnoreCase(request.getMethod(), "post")) {
            try {
                validateCode(new ServletWebRequest(request));
            } catch (ValidateCodeException e) {
                log.info("ValidateCodeFilter：验证码错误");
                //认证失败处理器返回验证码错误
                authenticationFailureHandler.onAuthenticationFailure(request,response,e);
                // 拦截请求，不往后传
                return;
            }
        }
        // 2 其他请求跳过此过滤器，往下传递
        filterChain.doFilter(request,response);
    }

    /**
     * 验证逻辑，没抛出异常则通过验证
     * @param request sessionStrategy.getAttribute()需要RequestAttributes的实现类的request
     */
    private void validateCode(ServletWebRequest request){
        // 1 从session中取得验证码
        ValidateCode codeInSession = (ValidateCode) sessionStrategy.getAttribute(request, ValidateCodeController.SESSION_KEY);

        // 2 取得用户提交的验证码
        String codeInRequest;
        try {
            codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(),SecurityConstants.DEFAULT_PARAMETER_NAME_CODE_IMAGE);
        } catch (ServletRequestBindingException e) {
            throw new ValidateCodeException("获取表单请求中的验证码的值失败");
        }
        // 3 校验验证码
        if (StringUtils.isBlank(codeInRequest)) {
            throw new ValidateCodeException("验证码的值不能为空");
        }
        if (codeInSession == null) {
            throw new ValidateCodeException("验证码不存在");
        }
        if (codeInSession.isExpried()) {
            sessionStrategy.removeAttribute(request, ValidateCodeController.SESSION_KEY);
            throw new ValidateCodeException("验证码已过期");
        }
        if (!StringUtils.equals(codeInSession.getCode(), codeInRequest)) {
            throw new ValidateCodeException("验证码不匹配");
        }
        // 不能在此清除session中的验证码，而是在登录成功后进行清除；否则验证码正确而密码错误后重新登录，验证码已被清除
    }

}
