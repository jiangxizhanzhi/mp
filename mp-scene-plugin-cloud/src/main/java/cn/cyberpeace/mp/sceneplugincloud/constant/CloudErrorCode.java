package cn.cyberpeace.mp.sceneplugincloud.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

public enum CloudErrorCode implements ErrorCode {
    CONNECTION_NOT_EXIST("MP-110700", "connection_not_exist"),
    INVALID_IP_ON_CIDR("MP-110701", "invalid_ip_on_cidr"),
    INVALID_IP_OR_CIDR("MP-110702", "invalid_ip_or_cidr"),
    FIRE_RULES_DATA_TOO_LONG("MP-110703", "fire_rules_data_too_long"),
    ENABLE_RECORDING_ERROR("MP-110704", "enable_recording_error"),
    NO_SPACE_RECORDING_ERROR("MP-110705", "no_space_recording_error");

    CloudErrorCode(String code, String message) {
        setCode(code, "sceneplugincloud.error_code." + message);
    }
}
