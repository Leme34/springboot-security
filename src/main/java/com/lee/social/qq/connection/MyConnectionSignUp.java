package com.lee.social.qq.connection;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Component;

/**
 * 第三方注册没有userId可以从{@link Connection}映射的情况下，默认注册新的第三方用户的逻辑
 */
@Component
public class MyConnectionSignUp implements ConnectionSignUp {

    @Override
    public String execute(Connection<?> connection) {
        // 使用第三方用户名作为userId去完成第三方注册
        return connection.getDisplayName();
    }

}
