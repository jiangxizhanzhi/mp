package cn.cyberpeace.mp.front.admin.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
 * @Description: 全局错误枚举
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public enum AdminErrorCode implements ErrorCode {
    PARAMS_ERROR("MP-110020", "params_error"),
    SYS_CONFIG_CANNOT_CLOSE_AUDIT("MP-110021", "sys_config_cannot_close_audit"),
    SYS_CONFIG_NOT_EXISTED("MP-110022", "sys_config_not_existed"),
    SYS_CONFIG_EXISTED("MP-110023", "sys_config_existed"),
    SYS_CONFIG_MODIFY_ERROR("MP-110024", "sys_config_modify_error"),


    SYS_ACTIVATION_ERROR("MP-110030", "sys_activation_error"),
    SYS_ACTIVATION_FAILED("MP-110031", "sys_activation_failed"),
    SYS_ACTIVATION_EXPIRED("MP-110032", "sys_activation_expired"),
    SYS_ACTIVATION_NONE("MP-110033", "sys_activation_none"),


    SYS_NOTICE_NOT_EXISTED("MP-110040", "sys_notice_not_existed");

    AdminErrorCode(String code, String message) {
        setCode(code, AdminConstant.ERROR_CODE_PREFIX + message);
    }
}
