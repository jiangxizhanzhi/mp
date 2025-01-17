 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogErrorCode.java.
 // File size 497.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;


/**
 * 错误码
 * @author wanggang
 */
public enum LogErrorCode implements ErrorCode {
    REQUEST_CLOUD_INTERFACE_BUSINESS_ERROR("MP-111001", "request_cloud_interface_business_error"),
    DOWNLOAD_LOG_FILE_ERROR("MP-111002", "download.log.file.error");

    LogErrorCode(String code, String message) {
        setCode(code, ControllerConstant.ERROR_CODE_PREFIX + message);
    }
}
