package cn.cyberpeace.mp.log.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;



public enum LogErrorCode implements ErrorCode {
    REQUEST_CLOUD_INTERFACE_BUSINESS_ERROR("MP-111001", "request_cloud_interface_business_error"),
    DOWNLOAD_LOG_FILE_ERROR("MP-111002", "download.log.file.error");

    LogErrorCode(String code, String message) {
        setCode(code, ControllerConstant.ERROR_CODE_PREFIX + message);
    }
}
