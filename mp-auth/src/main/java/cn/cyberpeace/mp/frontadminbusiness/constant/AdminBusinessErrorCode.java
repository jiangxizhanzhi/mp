 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AdminBusinessErrorCode.java.
 // File size 561.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadminbusiness.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
 * @ClassName AdminBusinessErrorCode
 * @Description 全局错误枚举
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see Enum
 * @see ErrorCode
 */
public enum AdminBusinessErrorCode implements ErrorCode {
    SYS_CONFIG_ERROR("MP-120001", "sys_config_error");

    AdminBusinessErrorCode(String code, String message) {
        setCode(code, AdminBusinessConstant.ERROR_CODE_PREFIX + message);
    }
}
