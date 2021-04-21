package cn.cyberpeace.mp.base.service.code;

import java.util.HashMap;
import java.util.Map;

public interface ServiceCode {

    Map<Integer, Code> codeMap = new HashMap<>();

    default void setCode(String code, String message) {
        Code innerCode = getInnerCode();
        innerCode.setCode(code);
        innerCode.setMessage(message);
    }

    default Code getInnerCode() {
        Integer key = this.hashCode();
        Code code;
        if (codeMap.containsKey(key)) {
            code = codeMap.get(key);
        } else {
            code = new Code();
            codeMap.put(key, code);
        }
        return code;
    }

    default String getCode() {
        return getInnerCode().getCode();
    }

    default String getMessage() {
        return getInnerCode().getMessage();
    }
}
