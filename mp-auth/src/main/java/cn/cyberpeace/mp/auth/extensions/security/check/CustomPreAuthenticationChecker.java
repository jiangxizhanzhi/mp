 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CustomPreAuthenticationChecker.java.
 // File size 962.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.extensions.security.check;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

 /**
  * 自定义预认证检查器
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Slf4j
public class CustomPreAuthenticationChecker implements UserDetailsChecker {
     /**
      * 检查
      *
      * @param user 用户
      */
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
