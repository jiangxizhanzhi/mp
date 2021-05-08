package cn.cyberpeace.mp.auth.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

public enum AuthErrorCode implements ErrorCode {
    INVALID_PARAM("AUTH-0001", "invalid_param"),
    NOT_CERTIFIED("AUTH-0002", "not_certified"),
    AUTHENTICATION_FAILED("AUTH-0003", "authentication_failed"),
    USERNAME_NOT_EXIST("AUTH-0004", "username_not_exist"),
    PASSWORD_NOT_MATCH("AUTH-0005", "password_not_match");

    AuthErrorCode(String code, String message) {
        setCode(code, "auth.error_code." + message);
    }
}
