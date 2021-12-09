package cn.cyberpeace.mp.front.admin.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
 * @Description: 全局错误枚举
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public enum AdminErrorCode implements ErrorCode {
    SYS_CONFIG_CANNOT_CLOSE_AUDIT("MP-110021", "sysconfig_cannot_close_audit"),
    SYS_CONFIG_NOT_EXIST("MP-110022", "sysconfig_notexist"),
    SYS_ACTIVATION_ERROR("MP-110023", "sys_activation_error"),
    SYS_CONFIG_ERROR("MP-110024", "sys_config_error");

    AdminErrorCode(String code, String message) {
        setCode(code, AdminConstant.ERROR_CODE_PREFIX + message);
    }
}
