package cn.cyberpeace.mp.sceneplugincloud.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

public enum CloudErrorCode implements ErrorCode {
    CONNECTION_NOT_EXIST("MP-110700", "connection_not_exist");

    CloudErrorCode(String code, String message) {
        setCode(code, "sceneplugincloud.error_code." + message);
    }
}
