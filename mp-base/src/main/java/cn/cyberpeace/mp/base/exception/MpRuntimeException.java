package cn.cyberpeace.mp.base.exception;

import lombok.Data;

/**
 * 监控运行时异常
 */
@Data
public class MpRuntimeException extends RuntimeException{
    private String message;
    private String errorCode;

    public MpRuntimeException(String message) {
        super(message);
        this.message = message;
    }
}
