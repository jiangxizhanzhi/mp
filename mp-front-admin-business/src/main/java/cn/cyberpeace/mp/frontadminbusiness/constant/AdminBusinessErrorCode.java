 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AdminBusinessErrorCode.java.
 // File size 474.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadminbusiness.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

 /**
  * 管理业务错误代码
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public enum AdminBusinessErrorCode implements ErrorCode {
    SYS_CONFIG_ERROR("MP-120001", "sys_config_error");

     /**
      * 管理业务错误代码
      *
      * @param code    代码
      * @param message 消息
      */
     AdminBusinessErrorCode(String code, String message) {
        setCode(code, AdminBusinessConstant.ERROR_CODE_PREFIX + message);
    }
}
