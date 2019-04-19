package com.lee.vo;

import com.lee.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 请求响应体的vo对象
 * 放入ResponseEntity中的body中返回前端
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HttpResponseResult {

    private Integer statusCode;
    private Object data;

    //返回成功信息时使用的方法
    public static HttpResponseResult ok(Object data){
        HttpResponseResult result = new HttpResponseResult();
        result.setStatusCode(200);
        result.setData(data);
        return result;
    }

    //返回错误信息时使用的方法
    public static HttpResponseResult error(ExceptionEnum em){
        HttpResponseResult result = new HttpResponseResult();
        result.setStatusCode(em.getStatusCode());
        result.setData(em.getMsg());
        return result;
    }

}
