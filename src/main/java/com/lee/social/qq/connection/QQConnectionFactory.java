package com.lee.social.qq.connection;

import com.lee.properties.MySecurityProperties;
import com.lee.social.qq.api.QQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

/**
 * 构建QQ登录的 ConnectionFactory
 * 由 ServiceProvider 和 ApiAdapter 组成
 */
public class QQConnectionFactory extends OAuth2ConnectionFactory<QQ> {

    /**
     * 创建一个 ConnectionFactory，必须提供构造函数
     * @param providerId  提供者ID，例如“facebook”
     */
    public QQConnectionFactory(String providerId, String appId, String appSecret) {
        // 使用自定义的serviceProvider和apiAdapter
        super(providerId, new QQServiceProvider(appId, appSecret), new QQAdapter());
    }

}
