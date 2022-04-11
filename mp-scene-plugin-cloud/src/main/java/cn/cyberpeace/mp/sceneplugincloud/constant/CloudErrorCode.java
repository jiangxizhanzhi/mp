package cn.cyberpeace.mp.sceneplugincloud.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

public enum CloudErrorCode implements ErrorCode {
    CONNECTION_NOT_EXIST("MP-110700", "connection_not_exist"),
    INVALID_IP_ON_CIDR("MP-110701", "invalid_ip_on_cidr"),
    INVALID_IP_OR_CIDR("MP-110702", "invalid_ip_or_cidr");

    CloudErrorCode(String code, String message) {
        setCode(code, "sceneplugincloud.error_code." + message);
    }
}
