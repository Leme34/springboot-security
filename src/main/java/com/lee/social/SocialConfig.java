package com.lee.social;

import com.lee.properties.MySecurityProperties;
import com.lee.social.qq.connection.MyConnectionSignUp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.SpringSocialConfigurer;

import javax.sql.DataSource;

@Configuration
@EnableSocial
public class SocialConfig extends SocialConfigurerAdapter {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private MySecurityProperties securityProperties;

    @Autowired(required = false)  //若ioc中没有则不注入
    private MyConnectionSignUp connectionSignUp;

    /**
     * 把 第三方登录的用户 与 此应用用户 的对应关系写入数据库
     * 需要先定位JdbcUsersConnectionRepository所在源码，然后它下边的执行JdbcUsersConnectionRepository.sql创建“userconnection”表
     *
     * @param connectionFactoryLocator 用于查找合适的ConnectionFactory
     */
    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        // 默认使用内存存储
        //return super.getUsersConnectionRepository(connectionFactoryLocator);

        // 使用jdbc，textEncryptor：配置存储的加解密方式，此处为了演示 不加密
        JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
        // 配置数据表“userconnection”的前缀（只允许增加前缀），此处修改为“security_userconnection”
        repository.setTablePrefix("security_");
        // 默认注册新的第三方用户的逻辑
        if (connectionSignUp != null) {
            repository.setConnectionSignUp(connectionSignUp);
        }
        return repository;
    }

    /**
     * 注入SpringSocialConfigurer对象
     * 用于在SecurityConfig中添加到Spring Security的过滤器链的配置器
     */
    @Bean
    public SpringSocialConfigurer mySocialSecurityConfig() {
        String filterProcessesUrl = securityProperties.getSocial().getFilterProcessesUrl();
        MySpringSocialConfigurer configurer = new MySpringSocialConfigurer(filterProcessesUrl);
        // 读取并配置注册页
        configurer.signupUrl(securityProperties.getBrowser().getSignUpUrl());
        return configurer;
    }


    /**
     * 注入获取第三方登录后session的工具类，用于用户注册页获取用户qq信息并存入数据库
     */
    @Bean
    public ProviderSignInUtils providerSignInUtils(ConnectionFactoryLocator connectionFactoryLocator) {
        return new ProviderSignInUtils(connectionFactoryLocator,
                getUsersConnectionRepository(connectionFactoryLocator));
    }

}
