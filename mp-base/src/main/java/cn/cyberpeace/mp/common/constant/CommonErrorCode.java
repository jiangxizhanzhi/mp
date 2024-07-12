 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CommonErrorCode.java.
 // File size 753.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.common.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
 * @ClassName CommonErrorCode
 * @Description common模块的错误码定义
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
 * @see ErrorCode
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
