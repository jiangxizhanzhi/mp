 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MpRuntimeException.java.
 // File size 1147.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:11 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.exception;

import cn.cyberpeace.mp.base.service.code.ServiceCode;
import lombok.Data;

/**
 * 监控运行时异常
 */
@Data
public class MpRuntimeException extends RuntimeException {

    private ServiceCode serviceCode;

    private Exception rawException;

    private String detail;

    public MpRuntimeException(String message) {
        super(message);
    }

    public MpRuntimeException(ServiceCode serviceCode) {
        super(serviceCode.transMessage());
        this.serviceCode = serviceCode;
    }

    public MpRuntimeException(ServiceCode serviceCode, String detail) {
        this(serviceCode);
        this.detail = detail;
    }

    public MpRuntimeException(ServiceCode serviceCode, Exception rawException) {
        this(serviceCode, rawException.getMessage());
    }

    public MpRuntimeException e(Exception rawException) {
        this.rawException = rawException;
        return this;
    }

    public MpRuntimeException de(Exception rawException) {
        this.detail = rawException.getMessage();
        return e(rawException);
    }

}
