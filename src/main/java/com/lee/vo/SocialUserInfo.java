/**
 * 
 */
package com.lee.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 授权后在注册或绑定页面显示获取到的第三方用户信息
 *
 */
@Data
@Builder
public class SocialUserInfo {
	
	private String providerId;
	private String providerUserId;
	private String nickname;
	private String headimg;

}
