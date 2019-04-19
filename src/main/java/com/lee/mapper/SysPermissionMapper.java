package com.lee.mapper;

import com.lee.pojo.SysPermission;
import com.lee.utils.MyMapper;

import java.util.List;

public interface SysPermissionMapper extends MyMapper<SysPermission> {

    /**
     * 根据角色id查询权限
     */
    List<SysPermission> getPermissionByRoleId(int roleId);

}