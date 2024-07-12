 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogErrorCode.java.
 // File size 609.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;


/**
 * @ClassName LogErrorCode
 * @Description 错误码
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
 * @see ErrorCode
 */
public enum LogErrorCode implements ErrorCode {
    REQUEST_CLOUD_INTERFACE_BUSINESS_ERROR("MP-111001", "request_cloud_interface_business_error"),
    DOWNLOAD_LOG_FILE_ERROR("MP-111002", "download.log.file.error");

    LogErrorCode(String code, String message) {
        setCode(code, ControllerConstant.ERROR_CODE_PREFIX + message);
    }
}
