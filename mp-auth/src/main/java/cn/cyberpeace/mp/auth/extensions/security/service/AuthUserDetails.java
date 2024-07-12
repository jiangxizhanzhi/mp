 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AuthUserDetails.java.
 // File size 1756.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.extensions.security.service;

import cn.cyberpeace.mp.auth.model.UserModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

/**
 * @ClassName AuthUserDetails
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see UserDetails
 */
public class AuthUserDetails implements UserDetails {

    private final UserModel user;

    private final Set<GrantedAuthority> authorities;

    public AuthUserDetails(UserModel user, Set<GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    public UserModel getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
//        return LocalDateTime.now().compareTo(user.getExpiredTime()) < 0;
        return true;
    }

    @Override
    // 用来代替是否已审核
    public boolean isAccountNonLocked() {
//        return Objects.equals(UserModel.Status.AUDITED, user.getIsAudited());
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
//        return Objects.equals(UserModel.Status.ENABLED, user.getIsEnabled());
        return true;
    }
}
