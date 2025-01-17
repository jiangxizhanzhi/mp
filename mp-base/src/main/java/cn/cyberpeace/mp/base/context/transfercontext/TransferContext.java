 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TransferContext.java.
 // File size 886.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.context.transfercontext;

import cn.cyberpeace.mp.base.common.BeanCommon;

import java.util.HashMap;
import java.util.Map;

public interface TransferContext<T> {
    String getKey();

    T getValue();

    void setValue(T value);

    static Map<String, Object> getContextMap() {
        Map<String, Object> contextMap = new HashMap<>();
        BeanCommon.getBeanType(TransferContext.class).forEach((name, transferContext) -> {
            contextMap.put(transferContext.getKey(), transferContext.getValue());
        });
        return contextMap;
    }

    static void setContextMap(Map<String, Object> contextMap) {
        BeanCommon.getBeanType(TransferContext.class).forEach((name, transferContext) -> {
            transferContext.setValue(contextMap.getOrDefault(transferContext.getKey(), null));
        });
    }
}
