package com.lee.mapper;

import com.lee.pojo.SysUser;
import com.lee.utils.MyMapper;

public interface SysUserMapper extends MyMapper<SysUser> {

    /**
     * 选择性插入 并返回mysql生成的id
     */
    int insertSelectiveForReturnId(SysUser sysUser);

    /**
     * 关联查询用户的角色权限信息
     */
    SysUser getUserByUserName(String userName);

}