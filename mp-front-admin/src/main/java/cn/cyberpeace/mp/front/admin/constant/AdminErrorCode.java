package cn.cyberpeace.mp.front.admin.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
 * @Description: 全局错误枚举
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public enum AdminErrorCode implements ErrorCode {
    PARAMS_ERROR("MP-110020", "params_error"),
    SYS_CONFIG_CANNOT_CLOSE_AUDIT("MP-110021", "sysconfig_cannot_close_audit"),
    SYS_CONFIG_NOT_EXISTED("MP-110022", "sysconfig_not_existed"),
    SYS_CONFIG_EXISTED("MP-110023", "sysconfig_existed"),
    SYS_ACTIVATION_ERROR("MP-110024", "sys_activation_error"),
    SYS_ACTIVATION_FAILED("MP-110025", "sys_activation_failed"),
    SYS_ACTIVATION_EXPIRED("MP-110026", "sys_activation_expired");

    AdminErrorCode(String code, String message) {
        setCode(code, AdminConstant.ERROR_CODE_PREFIX + message);
    }
}
