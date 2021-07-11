package cn.cyberpeace.mp.frontadminbusiness.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;


public enum AdminBusinessErrorCode implements ErrorCode {
    SYS_CONFIG_ERROR("MP-120001", "sys_config_error");

    AdminBusinessErrorCode(String code, String message) {
        setCode(code, AdminBusinessConstant.ERROR_CODE_PREFIX + message);
    }
}
