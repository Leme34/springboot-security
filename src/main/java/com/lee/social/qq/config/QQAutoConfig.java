package com.lee.social.qq.config;

import com.lee.properties.MySecurityProperties;
import com.lee.properties.QQProperties;
import com.lee.social.qq.connection.QQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.social.SocialAutoConfigurerAdapter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.connect.ConnectionFactory;

/**
 * 把QQ登录的自定义配置传给ConnectionFactory
 */
// 当且仅当配置文件中配置了security.social.qq.app-id时候此配置类才生效
@ConditionalOnProperty(prefix = "security.social.qq",name = "app-id")
@Configuration
public class QQAutoConfig extends SocialAutoConfigurerAdapter {

    @Autowired
    private MySecurityProperties securityProperties;

    @Override
    protected ConnectionFactory<?> createConnectionFactory() {
        QQProperties qqProperties = securityProperties.getSocial().getQq();
        String providerId = qqProperties.getProviderId();
        String appId = qqProperties.getAppId();
        String appSecret = qqProperties.getAppSecret();

        return new QQConnectionFactory(providerId, appId, appSecret);
    }

}
