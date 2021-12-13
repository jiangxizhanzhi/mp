package cn.cyberpeace.mp.auth.extensions.security.check;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
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
//            throw new LockedException(AuthConstant.ERROR_CODE_PREFIX + "account_locked");
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_LOCKED);
        }
        if (!user.isEnabled()) {
            log.info("Failed to authenticate since user account is disabled");
//            throw new DisabledException(AuthConstant.ERROR_CODE_PREFIX + "account_disabled");
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_DISABLED);
        }
        if (!user.isAccountNonExpired()) {
            log.info("Failed to authenticate since user account has expired");
//            throw new AccountExpiredException(AuthConstant.ERROR_CODE_PREFIX + "account_expired");
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_EXPIRED);
        }
    }
}
