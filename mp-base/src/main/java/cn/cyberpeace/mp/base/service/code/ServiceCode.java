package cn.cyberpeace.mp.base.service.code;

public interface ServiceCode {

    default void setCode(String code, String message) {
        var innerCode = getInnerCode();
        innerCode.setCode(code);
        innerCode.setMessage(message);
    }

    default String getCode() {
        return getInnerCode().getCode();
    }

    default String getMessage() {
        return getInnerCode().getMessage();
    }

    InnerCode getInnerCode();

}
