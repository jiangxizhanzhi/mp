package cn.cyberpeace.mp.auth.extensions.security.check;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Slf4j
public class CustomPostAuthenticationChecker implements UserDetailsChecker {

    @Override
    public void check(UserDetails user) {
        if (!user.isCredentialsNonExpired()) {
            log.info("Failed to authenticate since user account credentials have expired");
            throw new CredentialsExpiredException(AuthConstant.ERROR_CODE_PREFIX + "account_credentials_expired");
        }
    }
}
