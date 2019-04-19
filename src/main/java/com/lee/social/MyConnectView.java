/**
 * 
 */
package com.lee.social;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

/**
 * 绑定第三方帐号的请求地址：/connect/${providerId}  method="post"
 * 解绑的地址相同  method="delete"
 * 详见{@link org.springframework.social.connect.web.ConnectController}
 */
public class MyConnectView extends AbstractView {

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=UTF-8");
		// 若请求中没有携带connection信息 则是解绑
		if (model.get("connection") == null) {
			response.getWriter().write("<h3>解绑成功</h3>");
		} else { // 否则为绑定
			response.getWriter().write("<h3>绑定成功</h3>");
		}
	}

}
