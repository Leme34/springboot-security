package com.lee.social.qq.controller;

import com.lee.pojo.SysUser;
import com.lee.service.SysUserService;
import com.lee.vo.HttpResponseResult;
import com.lee.vo.SocialUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/social")
public class TestSocialController {

    @Autowired
    private SysUserService sysUserService;

    //获取授权后的用户信息的工具类
    @Autowired
    private ProviderSignInUtils providerSignInUtils;

    /**
     * 测试第三方登录的注册接口
     */
    @PostMapping("/signUp")
    public HttpResponseResult signUp(SysUser sysUser,HttpServletRequest request) {
        // 不管是注册或绑定都需要一个用户唯一标识（此处使用username）
        String userId = sysUser.getUsername();
        // 会使用connectionRepository保存到第三方登录的用户表中,第三方认证时会调用loadUserByUsername(userId)查询用户信息
        providerSignInUtils.doPostSignUp(userId,new ServletWebRequest(request));
        return HttpResponseResult.ok("第三方注册成功");
    }

    /**
     * 授权后在注册或绑定页面显示获取到的第三方用户信息的接口
     */
    @GetMapping("/user")
    public HttpResponseResult getSocialUserInfo(HttpServletRequest request) {  //此处应结合jsr303校验
        // 从session中取第三方用户信息
        Connection<?> conn = providerSignInUtils.getConnectionFromSession(new ServletWebRequest(request));
        SocialUserInfo socialUserInfo = SocialUserInfo.builder()
                .headimg(conn.getImageUrl())
                .nickname(conn.getDisplayName())
                .providerId(conn.getKey().getProviderId())
                .providerUserId(conn.getKey().getProviderUserId())
                .build();
        return HttpResponseResult.ok(socialUserInfo);
    }


}
