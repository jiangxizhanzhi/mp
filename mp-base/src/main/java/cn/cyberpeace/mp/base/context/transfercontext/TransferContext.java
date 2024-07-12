 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TransferContext.java.
 // File size 1583.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.context.transfercontext;

import cn.cyberpeace.mp.base.common.BeanCommon;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName TransferContext
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface TransferContext<T> {
    /**
     * getKey
     *
     * @return
     */
    String getKey();

    /**
     * getValue
     *
     * @return
     */
    T getValue();

    /**
     * setValue
     *
     * @param value
     */
    void setValue(T value);

    /**
     * getContextMap
     *
     * @return
     */
    static Map<String, Object> getContextMap() {
        Map<String, Object> contextMap = new HashMap<>();
        for (Map.Entry<String, TransferContext> entry : BeanCommon.getBeanType(TransferContext.class).entrySet()) {
            String name = entry.getKey();
            TransferContext transferContext = entry.getValue();
            contextMap.put(transferContext.getKey(), transferContext.getValue());
        }
        return contextMap;
    }

    /**
     * setContextMap
     *
     * @param contextMap
     */
    static void setContextMap(Map<String, Object> contextMap) {
        for (Map.Entry<String, TransferContext> entry : BeanCommon.getBeanType(TransferContext.class).entrySet()) {
            String name = entry.getKey();
            TransferContext transferContext = entry.getValue();
            transferContext.setValue(contextMap.getOrDefault(transferContext.getKey(), null));
        }
    }
}
