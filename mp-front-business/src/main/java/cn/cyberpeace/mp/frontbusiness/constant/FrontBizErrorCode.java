 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FrontBizErrorCode.java.
 // File size 533.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

 /**
  * 前台错误码
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public enum FrontBizErrorCode implements ErrorCode {

    URL_AC_REQUEST_METHOD_NOT_MATCH("MP-110141", "url_request_method_not_match"),
    URL_AC_AUTHORITY_NOT_MATCH("MP-110142", "url_access_denied");

     /**
      * 前台错误码
      *
      * @param code    代码
      * @param message 消息
      */
     FrontBizErrorCode(String code, String message) {
        setCode(code, FrontBizConstant.ERROR_CODE_PREFIX + message);
    }

}
