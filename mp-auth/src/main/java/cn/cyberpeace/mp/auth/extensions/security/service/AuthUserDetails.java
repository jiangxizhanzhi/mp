package cn.cyberpeace.mp.auth.extensions.security.service;

import cn.cyberpeace.mp.auth.model.UserModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

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
        return LocalDateTime.now().compareTo(user.getExpiredTime()) < 0;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return UserModel.Status.NORMAL.equals(user.getStatus());
    }
}
