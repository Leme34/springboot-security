package com.lee.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 验证码错误抛出的异常
 * 继承security身份认证中抛出异常的基类
 */
public class ValidateCodeException extends AuthenticationException {

    public ValidateCodeException(String msg) {
        super(msg);
    }

}
