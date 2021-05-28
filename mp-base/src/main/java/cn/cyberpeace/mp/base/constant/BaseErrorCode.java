package cn.cyberpeace.mp.base.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
 * base模块的错误码定义
 */
public enum BaseErrorCode implements ErrorCode {
    SUCCESS("BASE-0000", "success"),
    ERROR("BASE-0001", "error"),
    PARAMS_INVALID("BASE-0002", "params_invalid"),
    DATA_EXISTS("BASE-0003", "data_exists"),
    DATA_NOT_EXIST("BASE-0004", "data_not_exist"),
    DATA_PARTIAL_SUCCESS("BASE-0005", "partial_success");

    BaseErrorCode(String code, String message) {
        setCode(code, "base.error_code." + message);
    }
}
