package cn.cyberpeace.mp.common.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
 * common模块的错误码定义
 */
public enum CommonErrorCode implements ErrorCode {
    UPLOAD_FILE_TYPE_ERROR("MP-110800", "upload_file_type_error"),
    FILE_DOWNLOAD_FAILED("MP-110801", "file_download_failed"),

    FILE_NOT_FOUND_ERROR("MP-110802", "file_not_found_error")
    ;


    /**
     * CommonErrorCode
     *
     * @param code
     * @param message
     */
    CommonErrorCode(String code, String message) {
        setCode(code, "common.error_code." + message);
    }
}
