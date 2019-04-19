package com.lee.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.social.SocialProperties;

@Data
public class QQProperties extends SocialProperties{

    //服务提供商的标识
    private String providerId = "qq";

}
