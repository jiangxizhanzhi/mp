package cn.cyberpeace.mp.base.service.code;

import cn.cyberpeace.mp.base.common.MessageCommon;

/**
 * @ClassName ServiceCode
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface ServiceCode {

    default void setCode(String code, String message) {
        InnerCode innerCode = getInnerCode();
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

    default String transMessage() {
        return MessageCommon.get(getMessage());
    }

    default MessageServiceCode format(String... params) {
        return new MessageServiceCode(this, params);
    }

}
