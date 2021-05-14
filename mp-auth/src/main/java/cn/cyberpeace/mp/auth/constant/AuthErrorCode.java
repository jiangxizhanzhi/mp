package cn.cyberpeace.mp.auth.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

public enum AuthErrorCode implements ErrorCode {
    NOT_CERTIFIED("AUTH-0001", "not_certified"),
    AUTHENTICATION_FAILED("AUTH-0002", "authentication_failed");

    AuthErrorCode(String code, String message) {
        setCode(code, "auth.error_code." + message);
    }
}
