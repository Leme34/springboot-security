package com.lee.properties;

import lombok.Data;

/**
 * 验证码配置
 */
@Data
public class ValidateCodeProperties {
	
	private ImageCodeProperties image = new ImageCodeProperties();

}
