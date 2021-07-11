package cn.cyberpeace.mp.frontadmin.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;


public enum AdminErrorCode implements ErrorCode {
    PARAMS_ERROR("MP-110020", "params_error"),
    SYS_CONFIG_CANNOT_CLOSE_AUDIT("MP-110021", "sys_config_cannot_close_audit"),
    SYS_CONFIG_NOT_EXISTED("MP-110022", "sys_config_not_existed"),
    SYS_CONFIG_EXISTED("MP-110023", "sys_config_existed"),
    SYS_CONFIG_MODIFY_ERROR("MP-110024", "sys_config_modify_error"),


    SYS_ACTIVATION_STATUS_ERROR("MP-110029", "sys_activation_status_error"),
    SYS_ACTIVATION_ERROR("MP-110030", "sys_activation_error"),
    SYS_ACTIVATION_FAILED("MP-110031", "sys_activation_failed"),
    SYS_ACTIVATION_EXPIRED("MP-110032", "sys_activation_expired"),
    SYS_ACTIVATION_NONE("MP-110033", "sys_activation_none"),
    SYS_ACTIVATION_SYSUUID_NOTMATCH("MP-110034", "sys_activation_sysuuid_notmatch"),
    TRIAL_USE_OUT_OF_DATE("MP-110036", "trial_use_out_of_date"),


    SYS_NOTICE_NOT_EXISTED("MP-110040", "sys_notice_not_existed"),

    SYS_TIMESTAMP_ROLLBACK("MP-110050", "sys_timestamp_rollback"),

    OPTION_NAME_EXIST("MP-110060", "option_name_exist");

    AdminErrorCode(String code, String message) {
        setCode(code, AdminConstant.ERROR_CODE_PREFIX + message);
    }
}
