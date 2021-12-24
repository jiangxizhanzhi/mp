package cn.cyberpeace.mp.auth.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
 * @Description: 全局错误枚举
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public enum AuthErrorCode implements ErrorCode {
    OP_ERROR("MP-110000", "op_error"),
    NOT_CERTIFIED("MP-110001", "not_certified"),
    AUTHENTICATION_FAILED("MP-110002", "authentication_failed"),
    BAD_CREDENTIALS("MP-110003", "bad_credentials"),
    TOKEN_EXPIRED("MP-110004", "token_expired"),
    NEED_UPDATE_CREDENTIALS("MP-110005", "need_update_credentials"),
    ACCOUNT_NOT_LOGIN("MP-110006", "account_not_login"),
    ACCOUNT_CREDENTIALS_EXPIRED("MP-110007", "account_credentials_expired"),
    ACCOUNT_STATUS_ERROR("MP-110008", "account_status_error"),

    ACCOUNT_DISABLED("MP-110010", "account_disabled"),
    ACCOUNT_NOT_AUDITED("MP-110011", "account_not_audited"),
    ACCOUNT_EXPIRED("MP-110012", "account_expired"),
    ACCOUNT_TYPE_ERROR("MP-110013", "account_type_error"),
    ACCOUNT_NOT_EXISTED("MP-110014", "account_not_existed"),
    ACCOUNT_LOCKED("MP-110014", "account_locked"),
    USER_TARGET_DELETE_ERROR("MP-110015", "user_target_delete_error"),
    USER_TARGET_ADD_ERROR("MP-110016", "user_target_add_error"),
    USER_ROLE_DELETE_ERROR("MP-110017", "user_role_delete_error"),
    USER_ROLE_ADD_ERROR("MP-110018", "user_role_add_error"),
    USER_ORG_DELETE_ERROR("MP-110019", "user_organization_delete_error"),
    USER_ORG_ADD_ERROR("MP-110020", "user_organization_add_error"),
    USER_ADD_ERROR("MP-110021", "user_add_error"),
    USER_DELETE_ERROR("MP-110022", "user_delete_error"),
    USER_MODIFY_ERROR("MP-110023", "user_modify_error"),
    USER_CANNOT_DELETE_ADMIN("MP-110024", "user_cannot_delete_admin"),


    ACCOUNT_MOBILE_EXISTED("MP-110041", "mobile_existed"),
    ACCOUNT_USERNAME_EXISTED("MP-110042", "username_existed"),
    ACCOUNT_IDENTIFIER_EXISTED("MP-110043", "identifier_existed"),

    PASSWORD_STRENGTH_INVALID("MP-110044", "password_strength_invalid"),
    EMAIL_INVALID("MP-110045", "email_invalid"),
    MOBILE_INVALID("MP-110046", "mobile_invalid"),
    INCORRECT_OLD_CREDENTIALS("MP-110047", "incorrect_old_credentials"),


    // org
    ORG_NOT_EXISTED("MP-110070", "org_not_existed"),
    ORG_CHILD_ALREADY_EXISTED("MP-110071", "org_child_already_existed"),
    ORG_CANNOT_DELETE_WITH_CHILD_EXISTED("MP-110072", "org_cannot_delete_with_child_existed"),
    ORG_CANNOT_DELETE_WITH_USER_EXISTED("MP-110073", "org_cannot_delete_with_user_existed"),
    ORG_TARGET_DELETE_ERROR("MP-110074", "org_target_delete_error"),
    ORG_TARGET_ADD_ERROR("MP-110075", "org_target_add_error"),
    ORG_ADD_ERROR("MP-110076", "org_add_error"),
    ORG_DELETE_ERROR("MP-110077", "org_delete_error"),
    ORG_MODIFY_ERROR("MP-110078", "org_modify_error"),


    // role
    ROLE_NOT_EXISTED("MP-110080", "role_not_existed"),
    ROLE_IDENT_CANNOT_BE_ADMIN("MP-110081", "role_ident_cannot_be_admin"),
    ROLE_CANNOT_DELETE_WITH_USER_EXISTED("MP-110082", "role_cannot_delete_with_user_existed"),
    ROLE_CANNOT_DELETE_ADMIN("MP-110083", "role_cannot_delete_admin"),
    ROLE_TARGET_DELETE_ERROR("MP-110084", "role_target_delete_error"),
    ROLE_TARGET_ADD_ERROR("MP-110085", "role_target_add_error"),
    ROLE_ADD_ERROR("MP-110086", "role_add_error"),
    ROLE_DELETE_ERROR("MP-110087", "role_delete_error"),
    ROLE_MODIFY_ERROR("MP-110088", "role_modify_error"),
    ROLE_NAME_EXISTED("MP-110089", "role_name_existed"),


    // target
    TARGET_NOT_EXISTED("MP-110090", "target_not_existed"),
    TARGET_TYPE_NOT_SUPPORT("MP-110091", "target_type_not_support"),


    // validation-api params
    PARAMS_SHOULD_NOT_NULL("MP-110100", "params_should_not_null"),
    PARAMS_SHOULD_NOT_EMPTY("MP-110101", "params_should_not_empty"),


    // EXCEL
    EXCEL_EXCEED_SIZE("MP-110118", "excel_exceed_size"),
    EXCEL_FORMAT_ERROR("MP-110119", "excel_format_error"),
    EXCEL_NOT_EXCEL("MP-110120", "excel_not_excel"),
    EXCEL_USER_LIST_EMPTY("MP-110121", "excel_list_empty"),
    EXCEL_LIST_SIZE_OVER("MP-110122", "excel_list_size_over"),
    EXCEL_LIST_USERNAME_EMPTY("MP-110123", "excel_list_username_empty"),
    EXCEL_LIST_USERNAME_ILLEGAL_LENGTH("MP-110124", "excel_list_username_illegal_length"),
    EXCEL_LIST_USERNAME_EXISTED("MP-110125", "excel_list_username_existed"),
    EXCEL_LIST_NAME_EMPTY("MP-110126", "excel_list_name_empty"),
    EXCEL_LIST_NAME_ILLEGAL_LENGTH("MP-110127", "excel_list_name_illegal_length"),
    EXCEL_LIST_PWD_INVALID("MP-110128", "excel_list_pwd_invalid"),
    EXCEL_TEMPLATE_DOWNLOAD_ERROR("MP-110129", "excel_template_download_error");


    AuthErrorCode(String code, String message) {
        setCode(code, AuthConstant.ERROR_CODE_PREFIX + message);
    }
}
