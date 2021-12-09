package cn.cyberpeace.mp.front.admin.business.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
 * @Description: 全局错误枚举
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public enum AdminBusinessErrorCode implements ErrorCode {
    SYS_CONFIG_ERROR("MP-120001", "sys_config_error");

    AdminBusinessErrorCode(String code, String message) {
        setCode(code, AdminBusinessConstant.ERROR_CODE_PREFIX + message);
    }
}
