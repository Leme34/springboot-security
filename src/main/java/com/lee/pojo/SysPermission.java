package com.lee.pojo;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

/**
 * 权限实体，继承GrantedAuthority接口,用于security的角色权限校验
 */
@Data
@Table(name = "sys_permission")
public class SysPermission implements GrantedAuthority {
    /**
     * 权限编号
     */
    @Id
    private Integer id;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限描述
     */
    private String desc;

    /**
     * 权限url路径
     */
    private String url;

    /**
     * 父id
     */
    private Integer pid;

    /**
     * 权限类型标识，resourceId-权限name，两者结合进行权限校验
     */
    private String resourceId;

    /**
     * GrantedAuthority接口的方法，用于security的角色权限校验
     * @return 返回权限名称
     */
    @Override
    public String getAuthority() {
        return name;
    }
}