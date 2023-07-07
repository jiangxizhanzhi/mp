package cn.cyberpeace.mp.sceneplugincloud.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

public enum CloudErrorCode implements ErrorCode {
    CONNECTION_NOT_EXIST("MP-110700", "connection_not_exist"),
    INVALID_IP_ON_CIDR("MP-110701", "invalid_ip_on_cidr"),
    INVALID_IP_OR_CIDR("MP-110702", "invalid_ip_or_cidr"),
    FIRE_RULES_DATA_TOO_LONG("MP-110703", "fire_rules_data_too_long"),
    ENABLE_RECORDING_ERROR("MP-110704", "enable_recording_error"),
    NO_SPACE_RECORDING_ERROR("MP-110705", "no_space_recording_error"),
    IP_CONFLICT("MP-110706", "ip_conflict"),
    SNAPSHOT_BE_USED("MP-110707", "snapshot_be_used"),
    UN_SUPPORT_MULTI_NET("MP-110708", "un_support_multi_net"),

    IP_CONFLICT_WITH_SERVER("MP-110708", "ip_conflict_with_server"),
    NODE_CREATE_TIMEOUT("MP-110709", "node_create_timeout"),
    OPERATING_ERRORS("MP-110710", "operating_errors"),
    UN_SUPPORT_STATIC_ROUTE("MP-110711", "un_support_static_route"),
    UN_SUPPORT_OPERATE("MP-110712", "un_support_operate");

    CloudErrorCode(String code, String message) {
        setCode(code, "sceneplugincloud.error_code." + message);
    }
}
