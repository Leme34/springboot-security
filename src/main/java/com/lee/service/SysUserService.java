package com.lee.service;

import com.lee.pojo.SysUser;

public interface SysUserService {

    /**
     * 查询用户信息
     */
    SysUser querySysUserBySysUsername(String sysUsername);
    
    /**h
     * 注册用户
     */
    void saveSysUser(SysUser sysUser);

    /**
     * 查询该邮箱是否已被注册
     */
    boolean emailIsRegisted(String email);

    /**
     * 用户注册
     */
    void register(SysUser sysUser);


}
