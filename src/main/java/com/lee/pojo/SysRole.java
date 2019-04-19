package com.lee.pojo;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "sys_role")
public class SysRole {
    /**
     * 角色编号
     */
    @Id
    private Integer id;

    /**
     * 角色名称
     */
    private String name;

}