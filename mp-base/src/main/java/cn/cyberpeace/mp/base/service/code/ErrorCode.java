package cn.cyberpeace.mp.base.service.code;

import java.util.HashMap;
import java.util.Map;

/**
 * 错误码接口，定义服务错误码枚举实现本接口
 * 通过构造函数setCode
 */
public interface ErrorCode extends ServiceCode {

    @SuppressWarnings("java:S2386")
    Map<Integer, InnerCode> codeMap = new HashMap<>();

    @Override
    default InnerCode getInnerCode() {
        Integer key = this.hashCode();
        InnerCode innerCode;
        if (codeMap.containsKey(key)) {
            innerCode = codeMap.get(key);
        } else {
            innerCode = new InnerCode();
            codeMap.put(key, innerCode);
        }
        return innerCode;
    }
}
