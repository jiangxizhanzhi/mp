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


@ControllerAdvice(basePackages = {"cn.cyberpeace.mp.auth", "cn.cyberpeace.mp.front", "cn.cyberpeace.mp.frontadmin"})
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class AuthExceptionHandler {


    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ServiceResult<String> accessDeniedExceptionHandle(AccessDeniedException ex) {
        UserModel user = UserUtils.getLoginUser();
        log.error("access denied! {}, user:{}", ex.getMessage(), Objects.nonNull(user) ? user.getUsername() : "");
        return ServiceResult.<String>builder().serviceCode(AuthErrorCode.PERMISSION_DENIED).build().asResult();
    }

}
