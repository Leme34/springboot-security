package com.lee.controller;

import com.lee.pojo.SysUser;
import com.lee.service.SysUserService;
import com.lee.vo.HttpResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试注册和权限校验
 */
@Slf4j
@RestController
public class TestController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/register")
    public HttpResponseResult register(SysUser sysUser) {  //此处应结合jsr303校验
        sysUserService.register(sysUser);
        return new HttpResponseResult(200, "success");
    }

    /**
     * 使用 hasPermission 或者 hasRole 其一来校验权限
     * 因为底层都是调用authentication.getAuthorities()获取loadUserByUsername()添加的权限
     * bug：若用户权限不足会变为匿名用户而跳转登录页，而不是由MyAccessDeniedHandler返回权限不足，使用spring-session-data-redis则没问题
     */
    @PreAuthorize("hasPermission('user','write') or hasRole('ROLE_ADMIN')")
    @RequestMapping("/test1")
    public HttpResponseResult test1() {
        return new HttpResponseResult(200, "test1");
    }

    @PreAuthorize("hasPermission('user','read') or hasRole('ROLE_USER')")
    @RequestMapping("/test2")
    public HttpResponseResult test2() {
        return new HttpResponseResult(200, "test2");
    }

    /**
     * 查看当前登录的用户信息
     */
    @RequestMapping("/me")
    public HttpResponseResult currentUser(Authentication authentication) {
        return new HttpResponseResult(200, authentication);
    }

}
