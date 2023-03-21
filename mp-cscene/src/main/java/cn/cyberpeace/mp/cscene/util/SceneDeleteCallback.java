package cn.cyberpeace.mp.cscene.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description
 * @Author zhanglin
 * @Date 2023/3/21
 */
@Component
public class SceneDeleteCallback {
    public Map<Integer, String> mock = new HashMap<>();
    private Map<String, List<Runnable>> callbacks = new ConcurrentHashMap<>();

    public void addCallback(String id, Runnable callback) {

    }

    public void doCallback(String id) {
        if (callbacks.containsKey(id)) {
            callbacks.get(id).forEach(r -> {
                r.run();
            });
        }
    }
}
