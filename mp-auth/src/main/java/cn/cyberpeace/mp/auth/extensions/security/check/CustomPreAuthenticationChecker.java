package cn.cyberpeace.mp.auth.extensions.security.check;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Slf4j
public class CustomPreAuthenticationChecker implements UserDetailsChecker {
    @Override
    public void check(UserDetails user) {
        if (!user.isAccountNonLocked()) {
            log.info("Failed to authenticate since user account is locked");
            throw new LockedException(AuthConstant.ERROR_CODE_PREFIX + "account_locked");
        }
        if (!user.isEnabled()) {
           log.info("Failed to authenticate since user account is disabled");
            throw new DisabledException(AuthConstant.ERROR_CODE_PREFIX + "account_disabled");
        }
        if (!user.isAccountNonExpired()) {
            log.info("Failed to authenticate since user account has expired");
            throw new AccountExpiredException(AuthConstant.ERROR_CODE_PREFIX + "account_expired");
        }
    }
}
