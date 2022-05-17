package cn.cyberpeace.mp.base.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
 * base模块的错误码定义
 */
public enum BaseErrorCode implements ErrorCode {
    SUCCESS("MP-000000", "success"),
    ERROR("MP-099999", "error"),
    PARAMS_INVALID("MP-000001", "params_invalid"),
    DATA_EXISTS("MP-000002", "data_exists"),
    DATA_NOT_EXIST("MP-000003", "data_not_exist"),
    DATA_PARTIAL_SUCCESS("MP-000004", "partial_success"),
    GATEWAY_TIME_OUT("MP-000005", "gateway_time_out"),
    FIELD_VALUE_INVALID("MP-000006", "field_value_invalid"),
    FIELD_TYPE_INVALID("MP-000007", "file_type_invalid");

    BaseErrorCode(String code, String message) {
        setCode(code, "base.error_code." + message);
    }
}
