 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MpRuntimeException.java.
 // File size 1834.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.exception;

import cn.cyberpeace.mp.base.service.code.ServiceCode;
import lombok.Data;

/**
 * @ClassName MpRuntimeException
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see RuntimeException
 */
@Data
public class MpRuntimeException extends RuntimeException {

    private ServiceCode serviceCode;

    private Exception rawException;

    private String detail;

    /**
     * MpRuntimeException
     *
     * @param message
     */
    public MpRuntimeException(String message) {
        super(message);
    }

    /**
     * MpRuntimeException
     *
     * @param serviceCode
     */
    public MpRuntimeException(ServiceCode serviceCode) {
        super(serviceCode.transMessage());
        this.serviceCode = serviceCode;
    }

    /**
     * MpRuntimeException
     *
     * @param serviceCode
     * @param detail
     */
    public MpRuntimeException(ServiceCode serviceCode, String detail) {
        this(serviceCode);
        this.detail = detail;
    }

    /**
     * MpRuntimeException
     *
     * @param serviceCode
     * @param rawException
     */
    public MpRuntimeException(ServiceCode serviceCode, Exception rawException) {
        this(serviceCode, rawException.getMessage());
    }

    /**
     * e
     *
     * @param rawException
     * @return 文件名：Example.java
     */
    public MpRuntimeException e(Exception rawException) {
        this.rawException = rawException;
        return this;
    }

    /**
     * de
     *
     * @param rawException
     * @return 文件名：Example.java
     */
    public MpRuntimeException de(Exception rawException) {
        this.detail = rawException.getMessage();
        return e(rawException);
    }

}
