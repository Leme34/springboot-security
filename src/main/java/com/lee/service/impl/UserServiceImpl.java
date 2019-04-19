package com.lee.service.impl;

import com.lee.enums.ExceptionEnum;
import com.lee.exception.MyException;
import com.lee.mapper.SysPermissionMapper;
import com.lee.mapper.SysUserMapper;
import com.lee.mapper.SysUserRoleMapper;
import com.lee.pojo.SysPermission;
import com.lee.pojo.SysUser;
import com.lee.pojo.SysUserRole;
import com.lee.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 实现UserDetailsService的loadUserByUsername接口，为认证用户提供用户信息（密码）
 */
@Slf4j
@Service
public class UserServiceImpl implements SysUserService, UserDetailsService, SocialUserDetailsService {
    // 默认普通用户角色
    private final static Integer DEFAULT_USER_ROLE = 2;

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysPermissionMapper sysPermissionMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    // 手动注入的 BCrypt 编码器
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public SysUser querySysUserBySysUsername(String sysUsername) {
        return sysUserMapper.getUserByUserName(sysUsername);
    }

    @Override
    public void saveSysUser(SysUser sysUser) {
        sysUserMapper.insertSelective(sysUser);
    }

    @Override
    public boolean emailIsRegisted(String email) {
        SysUser user = new SysUser();
        user.setEmail(email);
        List<SysUser> users = sysUserMapper.select(user);
        return CollectionUtils.isNotEmpty(users);
    }

    @Transactional
    @Override
    public void register(SysUser sysUser) {
        // 1 验证邮箱是否重复
        if (emailIsRegisted(sysUser.getEmail())) {
            throw new MyException(ExceptionEnum.EMAIL_REPEATED);
        }
        // 2 密码编码
        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        // 3 用户入库
        int userId = sysUserMapper.insertSelectiveForReturnId(sysUser);
        // 4 默认角色权限入库
        SysUserRole userRole = SysUserRole.builder()
                .userId(userId)
                .roleId(DEFAULT_USER_ROLE)
                .createTime(new Date())
                .build();
        sysUserRoleMapper.insertSelective(userRole);
    }

    /**
     * security先调用UserDetailsService的loadUserByUsername(String username)认证 再调用UserDetails的getAuthorities()拿权限
     * 认证逻辑，登录成功保留到session中
     *
     * @return 查出用户信息并返回继承了UserDetail的User对象，
     * 若不存在该用户则抛出异常，但security规定不能返回null
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1 查询用户，security会调用UserDetails的getPassword()使用自定义密码编码器认证用户，且关联查询出角色信息
        SysUser user = sysUserMapper.getUserByUserName(username);
        // 抛出不存在此用户的异常
        if (user == null) {
            log.info(String.format("用户: %s不存在", username));
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        // 2 根据角色查询所有权限名称（去重）
        Set<SysPermission> permissionSet = new HashSet<>();
        user.getRoles().forEach(role -> {
            // 查询此角色拥有的权限
            List<SysPermission> sysPermissions = sysPermissionMapper.getPermissionByRoleId(role.getId());
            permissionSet.addAll(sysPermissions);
        });
        // 3 权限转为List<SimpleGrantedAuthority> 交给security管理
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        permissionSet.forEach(permission ->
                // resourceId-权限name，两者结合进行权限校验
                authorities.add(new SimpleGrantedAuthority(String.format("%s-%s", permission.getResourceId(), permission.getAuthority())))
        );
        log.info("当前用户权限：" + authorities);
        // 返回带有权限的org.springframework.security.core.userdetails.User对象
        return new User(user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked(), authorities);
    }

    /**
     * TODO 第三方登录时调用loadUserByUserId方法来认证用户
     */
    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        String password = passwordEncoder.encode("123");
        log.info("第三方用户登录的密码：" + password);
        return new SocialUser(userId,password,true,true,true,true,
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }
}
