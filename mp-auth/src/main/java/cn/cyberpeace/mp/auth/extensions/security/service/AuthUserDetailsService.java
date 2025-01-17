 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AuthUserDetailsService.java.
 // File size 4132.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.extensions.security.service;

import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

 /**
  * 身份验证用户细节服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Slf4j
@Service
@CacheConfig(cacheManager = AuthCacheConfig.CACHE_MANAGER_NORMAL)
public class AuthUserDetailsService implements UserDetailsService {

     /**
      *
      */
     public static final String CACHE_USER_AUTHORITIES = "USER_AUTHORITIES_CACHE";

     /**
      * 用户服务
      */
     private final UserService userService;

     /**
      * 用户role服务
      */
     @Autowired
    private UserRoleService userRoleService;

     /**
      * 用户permission服务
      */
     @Autowired
    private UserPermissionService userPermissionService;

     /**
      * 身份验证用户细节服务
      *
      * @param userService 用户服务
      */
     @Autowired
    public AuthUserDetailsService(UserService userService) {
        this.userService = userService;
    }

     /**
      * 按用户名加载用户
      *
      * @param username 用户名
      * @return {@link UserDetails }
      * @throws UsernameNotFoundException 用户名未发现异常
      */
     @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isEmpty(username)) {
            throw new UsernameNotFoundException(StringUtils.EMPTY);
        }

        UserModel user;
        try {
            user = userService.getByUserName(username);
        } catch (Exception e) {
            log.error("Get user by username[{}] error: {}", username, e.getMessage());
            throw new UsernameNotFoundException(e.getMessage());
        }
        if (user == null) {
            throw new UsernameNotFoundException(StringUtils.EMPTY);
        }

        Set<GrantedAuthority> authoritySet = loadAuthorities(user.getId());

        return new AuthUserDetails(user, authoritySet);
    }

     /**
      * 按移动设备加载用户
      *
      * @param mobile 移动
      * @return {@link UserDetails }
      */
     public UserDetails loadUserByMobile(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            throw new UsernameNotFoundException(StringUtils.EMPTY);
        }

        UserModel user;
        try {
            user = userService.getByMobile(mobile);
        } catch (Exception e) {
            log.error("Get user by mobile[{}] error: {}", mobile, e.getMessage());
            throw new UsernameNotFoundException(e.getMessage());
        }
        if (user == null) {
            throw new UsernameNotFoundException(StringUtils.EMPTY);
        }

        Set<GrantedAuthority> authoritySet = loadAuthorities(user.getId());

        return new AuthUserDetails(user, authoritySet);
    }

     /**
      * 负载当局
      *
      * @param userId
      * @return
      */
     @Cacheable(value = CACHE_USER_AUTHORITIES)
    public Set<GrantedAuthority> loadAuthorities(String userId) {
        Set<GrantedAuthority> authoritySet = new HashSet<>();
        if (StringUtils.isBlank(userId)) {
            return authoritySet;
        }
        List<RoleModel> roles = userRoleService.listByUserId(userId);
        roles.forEach(r -> {
            authoritySet.add(new SimpleGrantedAuthority("ROLE_" + r.getIdentifier()));
        });

        // 加载权限
        List<TargetModel> targets = userPermissionService.listTargetsByUserId(userId);
        targets.forEach(t -> {
            authoritySet.add(new SimpleGrantedAuthority(t.getIdentifier()));
        });
        return authoritySet;
    }


}
