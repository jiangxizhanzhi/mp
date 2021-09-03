package cn.cyberpeace.mp.auth.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
 * @Description: 全局错误枚举
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public enum AuthErrorCode implements ErrorCode {
    NOT_CERTIFIED("AUTH-0001", "not_certified"),
    AUTHENTICATION_FAILED("AUTH-0002", "authentication_failed"),
    BAD_CREDENTIALS("AUTH-0003", "bad_credentials"),
    TOKEN_EXPIRED("AUTH-0004", "token_expired"),
    NEED_UPDATE_CREDENTIALS("AUTH-0005", "need_update_credentials"),

    ACCOUNT_DISABLED("AUTH-0010", "account_disabled"),
    ACCOUNT_NOT_AUDITED("AUTH-0011", "account_not_audited"),
    ACCOUNT_EXPIRED("AUTH-0012", "account_expired"),

    SYS_CONFIG_CANNOT_CLOSE_AUDIT("AUTH-1001", "sysconfig_cannot_close_audit");

    AuthErrorCode(String code, String message) {
        setCode(code, AuthConstant.ERROR_CODE_PREFIX + message);
    }
}
