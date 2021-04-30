package cn.cyberpeace.mp.base.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

public enum BaseErrorCode implements ErrorCode {
    SUCCESS("COMMON-0000", "success"),
    ERROR("COMMON-0001", "error"),
    DATA_EXISTS("DATA-0001", "data_exists"),
    DATA_NOT_EXIST("DATA-0002", "data_not_exist"),
    DATA_PARTIAL_SUCCESS("DATA-0003", "partial_success");

    BaseErrorCode(String code, String message) {
        setCode(code, "base.error_code." + message);
    }
}
