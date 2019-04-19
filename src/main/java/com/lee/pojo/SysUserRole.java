package com.lee.pojo;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Builder
@Data
@Table(name = "sys_user_role")
public class SysUserRole {
    /**
     * 用户id
     */
    @Id
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 角色编号
     */
    @Id
    @Column(name = "role_id")
    private Integer roleId;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

}