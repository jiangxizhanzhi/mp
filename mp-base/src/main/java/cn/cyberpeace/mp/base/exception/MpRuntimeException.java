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
