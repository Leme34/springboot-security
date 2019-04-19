package com.lee.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lee.mapper.SysPermissionMapper;
import com.lee.mapper.SysUserMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Slf4j
@Data
@Table(name = "sys_user")
public class SysUser implements UserDetails {
    @Id
    @GeneratedValue(generator = "JDBC")  //返回mysql生成的id
    private int id;

    private String avatar;

    private String email;

    private String name;

    // security的UserDetails接口从此字段获取密码值使用自定义解码规则进行匹配
    @JsonIgnore  // 不返回给客户端
    private String password;

    private String username;

    // 关联查询该用户的权限
    private List<SysRole> roles;


    /**
     * 添加此用户的角色所拥有的权限
     * 若已在UserDetailsService的loadUserByUsername(String username)以返回org.springframework.security.core.userdetails.User对象来拿权限，则不使用此处返回null
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        log.info("SysUser.getAuthorities()----------------");
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}