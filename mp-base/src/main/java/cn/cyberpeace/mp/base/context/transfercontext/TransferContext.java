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
        for (Map.Entry<String, TransferContext> entry : BeanCommon.getBeanType(TransferContext.class).entrySet()) {
            String name = entry.getKey();
            TransferContext transferContext = entry.getValue();
            contextMap.put(transferContext.getKey(), transferContext.getValue());
        }
        return contextMap;
    }

    static void setContextMap(Map<String, Object> contextMap) {
        for (Map.Entry<String, TransferContext> entry : BeanCommon.getBeanType(TransferContext.class).entrySet()) {
            String name = entry.getKey();
            TransferContext transferContext = entry.getValue();
            transferContext.setValue(contextMap.getOrDefault(transferContext.getKey(), null));
        }
    }
}
