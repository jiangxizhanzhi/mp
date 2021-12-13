package cn.cyberpeace.mp.auth.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
 * @Description: 全局错误枚举
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public enum AuthErrorCode implements ErrorCode {
    NOT_CERTIFIED("MP-110001", "not_certified"),
    AUTHENTICATION_FAILED("MP-110002", "authentication_failed"),
    BAD_CREDENTIALS("MP-110003", "bad_credentials"),
    TOKEN_EXPIRED("MP-110004", "token_expired"),
    NEED_UPDATE_CREDENTIALS("MP-110005", "need_update_credentials"),

    ACCOUNT_DISABLED("MP-110010", "account_disabled"),
    ACCOUNT_NOT_AUDITED("MP-110011", "account_not_audited"),
    ACCOUNT_EXPIRED("MP-110012", "account_expired"),
    ACCOUNT_TYPE_ERROR("MP-110013", "account_type_error"),
    ACCOUNT_NOT_EXISTED("MP-110014", "account_not_existed"),


    ACCOUNT_MOBILE_EXISTED("MP-110041", "mobile_existed"),
    ACCOUNT_USERNAME_EXISTED("MP-110042", "username_existed"),
    ACCOUNT_IDENTIFIER_EXISTED("MP-110043", "identifier_existed"),

    PASSWORD_STRENGTH_INVALID("MP-110044", "password_strength_invalid"),
    EMAIL_INVALID("MP-110045", "email_invalid"),
    MOBILE_INVALID("MP-110046", "mobile_invalid"),


    // org
    ORG_NOT_EXISTED("MP-110070", "org_not_existed"),


    // role
    ROLE_NOT_EXISTED("MP-110080", "role_not_existed"),


    PARAMS_SHOULD_NOT_EMPTY("MP-110090", "params_should_not_empty"),


    // validation-api params
    PARAMS_USERNAME_EMPTY("MP-110100", "params.username_empty");


    AuthErrorCode(String code, String message) {
        setCode(code, AuthConstant.ERROR_CODE_PREFIX + message);
    }
}
