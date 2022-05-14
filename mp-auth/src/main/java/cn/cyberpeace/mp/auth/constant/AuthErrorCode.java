package cn.cyberpeace.mp.auth.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;


public enum AuthErrorCode implements ErrorCode {
    NOT_CERTIFIED("AUTH-0001", "not_certified"),
    AUTHENTICATION_FAILED("AUTH-0002", "authentication_failed"),
    BAD_CREDENTIALS("AUTH-0003", "bad_credentials"),
    TOKEN_EXPIRED("AUTH-0004", "token_expired"),
    NEED_UPDATE_CREDENTIALS("AUTH-0005", "need_update_credentials"),

    ACCOUNT_DISABLED("AUTH-0010", "account_disabled"),
    ACCOUNT_NOT_AUDITED("AUTH-0011", "account_not_audited"),
    ACCOUNT_EXPIRED("AUTH-0012", "account_expired"),

    SYS_CONFIG_CANNOT_CLOSE_AUDIT("AUTH-1001", "sysconfig_cannot_close_audit"),


    USER_CANNOT_DELETE_ADMIN("MP-110024", "user_cannot_delete_admin"),

    ROLE_CANNOT_DELETE_ADMIN("MP-110083", "role_cannot_delete_admin"),
    ROLE_CANNOT_DELETE_SUPERUSER("MP-110084", "role_cannot_delete_superuser"),

    PERMISSION_DENIED("MP-110140", "permission_denied");

    AuthErrorCode(String code, String message) {
        setCode(code, AuthConstant.ERROR_CODE_PREFIX + message);
    }
}
