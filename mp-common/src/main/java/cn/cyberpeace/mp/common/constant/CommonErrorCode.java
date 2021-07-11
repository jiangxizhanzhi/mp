package cn.cyberpeace.mp.common.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;


public enum CommonErrorCode implements ErrorCode {
    UPLOAD_FILE_TYPE_ERROR("MP-110800", "upload_file_type_error"),
    FILE_DOWNLOAD_FAILED("MP-110801", "file_download_failed")
    ;


    CommonErrorCode(String code, String message) {
        setCode(code, "common.error_code." + message);
    }
}
