package com.lee.properties;

import com.lee.common.SecurityConstants;
import lombok.Data;

/**
 * 浏览器相关配置
 */
@Data
public class BrowserProperties {

    private String signUpUrl = SecurityConstants.DEFAULT_SIGNUP_PAGE_URL;  // 默认注册页
    private String loginPage = SecurityConstants.DEFAULT_LOGIN_PAGE_URL;  // 默认登录页
    // security的记住我的有效时间默认为1周
    private int rememberMeSeconds = 3600*24*7;

}
