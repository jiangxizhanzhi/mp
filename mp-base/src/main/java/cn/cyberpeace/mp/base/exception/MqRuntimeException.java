package cn.cyberpeace.mp.base.exception;

import lombok.Data;

/**
 * 监控运行时异常
 */
@Data
public class MqRuntimeException extends RuntimeException{
    private String message;
    private String errorCode;

    public MqRuntimeException(String message) {
        super(message);
        this.message = message;
    }
}
