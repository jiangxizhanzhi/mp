package cn.cyberpeace.mp.collection.core.exception;

import lombok.Data;

@Data
public class MpCollectionRuntimeException extends RuntimeException {
    private String code;

    private Exception rawException;

    private String detail;

    public MpCollectionRuntimeException(String message) {
        super(message);
    }

    public MpCollectionRuntimeException(String code, String detail) {
        super(detail);
        this.code = code;
        this.detail = detail;
    }

    public MpCollectionRuntimeException(String code, Exception rawException) {
        this(code, rawException.getMessage());
    }
}
