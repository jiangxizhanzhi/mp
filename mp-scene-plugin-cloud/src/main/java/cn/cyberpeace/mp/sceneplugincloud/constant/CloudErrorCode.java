package cn.cyberpeace.mp.sceneplugincloud.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

public enum CloudErrorCode implements ErrorCode {
    CONNECTION_NOT_EXIST("MP-110700", "connection_not_exist"),
    INVALID_GROUP_COUNT_FIELD("MP-110701", "invalid_group_count_field");

    CloudErrorCode(String code, String message) {
        setCode(code, "sceneplugincloud.error_code." + message);
    }
}
