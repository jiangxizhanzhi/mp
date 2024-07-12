 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ResultCommon.java.
 // File size 1291.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.controller.common;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.code.ServiceCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;

/**
 * @ClassName ResultCommon
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class ResultCommon {

    /**
     * success
     *
     * @return
     */
    public static <T> ServiceResult<T> success() {
        return ServiceResult.<T>builder().build().asResult();
    }

    /**
     * success
     *
     * @param source
     * @return
     */
    public static <T> ServiceResult<T> success(T source) {
        return ServiceResult.<T>builder().source(source).build().asResult();
    }

    /**
     * fail
     *
     * @param errorCode
     * @return
     */
    public static <T1> ServiceResult<T1> fail(ServiceCode errorCode) {
        return ServiceResult.<T1>builder().serviceCode(errorCode).build().asResult();
    }

    /**
     * fail
     *
     * @param detail
     * @return
     */
    public static <T1> ServiceResult<T1> fail(String detail) {
        return ServiceResult.<T1>builder().serviceCode(BaseErrorCode.ERROR).detail(detail).build().asResult();
    }

}
