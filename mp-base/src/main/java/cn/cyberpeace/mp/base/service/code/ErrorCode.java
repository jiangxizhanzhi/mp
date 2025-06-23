 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ErrorCode.java.
 // File size 895.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.service.code;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName ErrorCode
 * @Description
 * 错误码接口，定义服务错误码枚举实现本接口
 * 通过构造函数setCode
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ServiceCode
 */
public interface ErrorCode extends ServiceCode {

    @SuppressWarnings("java:S2386")
    Map<Integer, InnerCode> codeMap = new HashMap<>();

    /**
     * getInnerCode
     *
     * @return
     */
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
