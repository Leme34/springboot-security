package com.lee.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum ExceptionEnum {

    EMAIL_REPEATED(500,"邮箱不能重复"),
    CREATE_IMAGE_CODE_FAIL(500,"生成图片验证码失败"),
    GET_QQ_USER_INFO_FAIL(500,"获取QQ用户信息失败"),
    UNAUTHORIZED(401,"没有权限访问，请先登录"),

    ;

    private Integer statusCode;
    private String msg;
}
