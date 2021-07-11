package cn.cyberpeace.mp.auth.extensions.security.check;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;


@Slf4j
public class CustomPreAuthenticationChecker implements UserDetailsChecker {
    @Override
    public void check(UserDetails user) {
        if (!user.isAccountNonLocked()) {
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_LOCKED);
        }
        if (!user.isEnabled()) {
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_DISABLED);
        }
        if (!user.isAccountNonExpired()) {
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_EXPIRED);
        }
    }
}
