package cn.cyberpeace.mp.auth.extensions.security.check;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;


@Slf4j
public class CustomPostAuthenticationChecker implements UserDetailsChecker {

    @Override
    public void check(UserDetails user) {
        if (!user.isCredentialsNonExpired()) {
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_CREDENTIALS_EXPIRED);
        }
    }
}
