package com.lee.properties;

import lombok.Data;

/**
 * 自定义的第三方登录相关配置
 */
@Data
public class MySocialProperties {
    // 默认值与SocialAuthenticationFilter的DEFAULT_FILTER_PROCESSES_URL保持一致
    private String filterProcessesUrl = "/auth";
    private QQProperties qq = new QQProperties();
    private WeixinProperties weixin = new WeixinProperties();

}
