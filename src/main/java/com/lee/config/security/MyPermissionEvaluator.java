package com.lee.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;

/**
 * 配置@PreAuthorize注解的hasPermission权限校验逻辑
 */
@Component
@Slf4j
public class MyPermissionEvaluator implements PermissionEvaluator {

    /**
     * 自定校验权限逻辑，resourceId-权限name，两者结合进行权限校验，匹配则认为有权限
     *
     * @targetDomainObject @PreAuthorize的hasPermission()的第一个参数 resourceId
     * @permission @PreAuthorize的hasPermission()的第二个参数 权限name
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

        boolean accessable = false;
        if (authentication.getPrincipal().toString().compareToIgnoreCase("anonymousUser") != 0) {
            // 若loadUserByUsername()中的SimpleGrantedAuthority 拼接逻辑是以"-"，使用：@PreAuthorize("hasPermission('user', 'read') or hasRole('ROLE_ADMINISTRATOR')")
            String privilege = targetDomainObject + "-" + permission;

            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (privilege.equalsIgnoreCase(authority.getAuthority())) {
                    accessable = true;
                    break;
                }
            }
            return accessable;
        }
        return accessable;
    }

    // ACL方式验权
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }


}
