package com.lee.pojo;

import javax.persistence.*;

@Table(name = "sys_role_permission")
public class SysRolePermission {
    @Id
    private Integer id;

    /**
     * 角色编号
     */
    @Column(name = "role_id")
    private Integer roleId;

    /**
     * 权限编号
     */
    @Column(name = "permission_id")
    private Integer permissionId;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取角色编号
     *
     * @return role_id - 角色编号
     */
    public Integer getRoleId() {
        return roleId;
    }

    /**
     * 设置角色编号
     *
     * @param roleId 角色编号
     */
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    /**
     * 获取权限编号
     *
     * @return permission_id - 权限编号
     */
    public Integer getPermissionId() {
        return permissionId;
    }

    /**
     * 设置权限编号
     *
     * @param permissionId 权限编号
     */
    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }
}