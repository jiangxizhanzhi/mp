package cn.cyberpeace.mp.base.util.thread.context;

import cn.cyberpeace.mp.base.util.BeanUtils;

import java.util.HashMap;
import java.util.Map;

public interface TransferContext<T> {

    T getValue();

    void setValue(T value);

    static Map<String, Object> getContextMap() {
        Map<String, Object> contextMap = new HashMap<>();
        BeanUtils.getBeanType(TransferContext.class).forEach((name, transferContext) -> {
            contextMap.put(name, transferContext.getValue());
        });
        return contextMap;
    }

    static void setContextMap(Map<String, Object> contextMap) {
        BeanUtils.getBeanType(TransferContext.class).forEach((name, transferContext) -> {
            transferContext.setValue(contextMap.getOrDefault(name, null));
        });
    }
}
