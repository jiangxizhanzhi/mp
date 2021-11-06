package cn.cyberpeace.mp.base.exception;

import cn.cyberpeace.mp.base.service.code.ServiceCode;
import lombok.Data;

/**
 * 监控运行时异常
 */
@Data
public class MpRuntimeException extends RuntimeException {

    private ServiceCode serviceCode;

    private String detail;

    public MpRuntimeException(String message) {
        super(message);
    }

    public MpRuntimeException(ServiceCode serviceCode) {
        super(serviceCode.getMessage());
        this.serviceCode = serviceCode;
    }

    public MpRuntimeException(ServiceCode serviceCode, String detail) {
        super(serviceCode.getMessage());
        this.serviceCode = serviceCode;
        this.detail = detail;
    }


}
