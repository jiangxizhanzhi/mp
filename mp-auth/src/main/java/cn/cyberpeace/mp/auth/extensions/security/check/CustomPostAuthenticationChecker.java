 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CustomPostAuthenticationChecker.java.
 // File size 885.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.extensions.security.check;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

/**
 * @ClassName CustomPostAuthenticationChecker
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see UserDetailsChecker
 */
@Slf4j
public class CustomPostAuthenticationChecker implements UserDetailsChecker {

    /**
     * check
     *
     * @param user
     */
    @Override
    public void check(UserDetails user) {
        if (!user.isCredentialsNonExpired()) {
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_CREDENTIALS_EXPIRED);
        }
    }
}
