 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CustomPostAuthenticationChecker.java.
 // File size 734.
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
  * 自定义邮件身份验证检查器
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Slf4j
public class CustomPostAuthenticationChecker implements UserDetailsChecker {

     /**
      * 检查
      *
      * @param user 用户
      */
     @Override
    public void check(UserDetails user) {
        if (!user.isCredentialsNonExpired()) {
            throw new MpRuntimeException(AuthErrorCode.ACCOUNT_CREDENTIALS_EXPIRED);
        }
    }
}
