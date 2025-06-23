 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseErrorCode.java.
 // File size 980.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
 * @ClassName BaseErrorCode
 * @Description base模块的错误码定义
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
 * @see ErrorCode
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
    CUSTOM_ERROR("MP-000007", "custom_error");

    /**
     * BaseErrorCode
     *
     * @param code
     * @param message
     */
    BaseErrorCode(String code, String message) {
        setCode(code, "base.error_code." + message);
    }
}
