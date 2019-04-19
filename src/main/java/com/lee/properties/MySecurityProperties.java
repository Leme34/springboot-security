package com.lee.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 总的自定义配置
 */
@Data
@ConfigurationProperties("security")
public class MySecurityProperties {
    // 浏览器相关配置
    private BrowserProperties browser = new BrowserProperties();
    // 验证码相关配置
    private ValidateCodeProperties code = new ValidateCodeProperties();
    // 第三方登录相关配置
    private MySocialProperties social = new MySocialProperties();

}
