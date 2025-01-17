 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CommonErrorCode.java.
 // File size 458.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.common.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
 * common模块的错误码定义
 */
public enum CommonErrorCode implements ErrorCode {
    UPLOAD_FILE_TYPE_ERROR("MP-110800", "upload_file_type_error"),
    FILE_DOWNLOAD_FAILED("MP-110801", "file_download_failed")
    ;


    CommonErrorCode(String code, String message) {
        setCode(code, "common.error_code." + message);
    }
}
