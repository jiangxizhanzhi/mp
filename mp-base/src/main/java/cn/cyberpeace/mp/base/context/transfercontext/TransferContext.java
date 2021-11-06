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
