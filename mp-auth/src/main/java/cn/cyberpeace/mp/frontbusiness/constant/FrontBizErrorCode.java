 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FrontBizErrorCode.java.
 // File size 710.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
 * @ClassName FrontBizErrorCode
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/5/21 17:36
 * @Version 1.0.0
 * @see Enum
 * @see ErrorCode
 */
public enum FrontBizErrorCode implements ErrorCode {

    URL_AC_REQUEST_METHOD_NOT_MATCH("MP-110141", "url_request_method_not_match"),
    URL_AC_AUTHORITY_NOT_MATCH("MP-110142", "url_access_denied");

    /**
     * FrontBizErrorCode
     *
     * @param code
     * @param message
     */
    FrontBizErrorCode(String code, String message) {
        setCode(code, FrontBizConstant.ERROR_CODE_PREFIX + message);
    }

}
