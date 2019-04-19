/**
 * 
 */
package com.lee.validate;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 验证码基类
 */
@Data
public class ValidateCode implements Serializable {
	
	private String code;

	// 过期的时间点 = 当前时间 + 过期时间
	private LocalDateTime expireTime;
	
	public ValidateCode(String code, int expireIn){
		this.code = code;
		this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
	}
	
	public ValidateCode(String code, LocalDateTime expireTime){
		this.code = code;
		this.expireTime = expireTime;
	}

	// 是否超出过期的时间点
	public boolean isExpried() {
		return LocalDateTime.now().isAfter(expireTime);
	}

}
