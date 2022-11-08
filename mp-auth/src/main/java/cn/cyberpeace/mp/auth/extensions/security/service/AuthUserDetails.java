package cn.cyberpeace.mp.auth.extensions.security.service;

import cn.cyberpeace.mp.auth.model.UserModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
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
