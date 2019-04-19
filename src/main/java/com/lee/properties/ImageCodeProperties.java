/**
 * 
 */
package com.lee.properties;

import lombok.Data;

/**
 * 图形验证码配置
 */
@Data
public class ImageCodeProperties {

	private int width = 67;
	private int height = 23;
	private int length = 4;
	private int expireIn = 60;

}
