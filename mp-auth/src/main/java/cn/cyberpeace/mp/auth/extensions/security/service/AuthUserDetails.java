 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AuthUserDetails.java.
 // File size 1688.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.extensions.security.service;

import cn.cyberpeace.mp.auth.model.UserModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

 /**
  * 验证用户详细信息
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public class AuthUserDetails implements UserDetails {

     /**
      * 用户
      */
     private final UserModel user;

     /**
      * 当局
      */
     private final Set<GrantedAuthority> authorities;

     /**
      * 验证用户详细信息
      *
      * @param user        用户
      * @param authorities 当局
      */
     public AuthUserDetails(UserModel user, Set<GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

     /**
      * 获取用户
      *
      * @return {@link UserModel }
      */
     public UserModel getUser() {
        return user;
    }

     /**
      * 得到当局
      *
      * @return {@link Collection }<{@link ? } {@link extends } {@link GrantedAuthority }>
      */
     @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

     /**
      * 得到密码
      *
      * @return {@link String }
      */
     @Override
    public String getPassword() {
        return user.getPassword();
    }

     /**
      * 获得用户名
      *
      * @return {@link String }
      */
     @Override
    public String getUsername() {
        return user.getUsername();
    }

     /**
      * @return boolean
      */
     @Override
    public boolean isAccountNonExpired() {
//        return LocalDateTime.now().compareTo(user.getExpiredTime()) < 0;
        return true;
    }

     /**
      * @return boolean
      */
     @Override
    // 用来代替是否已审核
    public boolean isAccountNonLocked() {
//        return Objects.equals(UserModel.Status.AUDITED, user.getIsAudited());
        return true;
    }

     /**
      * @return boolean
      */
     @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

     /**
      * @return boolean
      */
     @Override
    public boolean isEnabled() {
//        return Objects.equals(UserModel.Status.ENABLED, user.getIsEnabled());
        return true;
    }
}
