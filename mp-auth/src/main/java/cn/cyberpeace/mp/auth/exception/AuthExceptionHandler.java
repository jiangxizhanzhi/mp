 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AuthExceptionHandler.java.
 // File size 1583.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.exception;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Objects;

 /**
  * 验证异常处理程序
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @ControllerAdvice(basePackages = {"cn.cyberpeace.mp.auth", "cn.cyberpeace.mp.front", "cn.cyberpeace.mp.frontadmin"})
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class AuthExceptionHandler {

     /**
      * 拒绝访问异常句柄
      *
      * @param ex 交货
      * @return {@link ServiceResult }<{@link String }>
      */
     @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ServiceResult<String> accessDeniedExceptionHandle(AccessDeniedException ex) {
        UserModel user = UserUtils.getLoginUser();
        log.error("access denied! {}, user:{}", ex.getMessage(), Objects.nonNull(user) ? user.getUsername() : "");
        return ServiceResult.<String>builder().serviceCode(AuthErrorCode.PERMISSION_DENIED).build().asResult();
    }

}
