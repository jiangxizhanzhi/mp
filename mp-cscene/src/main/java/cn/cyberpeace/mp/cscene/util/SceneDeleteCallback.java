package cn.cyberpeace.mp.cscene.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @Description
 * @Author zhanglin
 * @Date 2023/3/21
 */
@Component
@Slf4j
public class SceneDeleteCallback {
    private Set<String> sceneIds = new ConcurrentSkipListSet<>();
    private Map<String, List<Runnable>> callbacks = new ConcurrentHashMap<>();
    private Map<Integer, String> status = new HashMap<>();

    public void addCallback(String id, Runnable callback) {
        this.sceneIds.add(id);
        List<Runnable> list = new ArrayList<>();
        list.add(callback);
        this.callbacks.put(id, new ArrayList<>(list));
    }

    public void doCallback(String id) {
        if(callbacks.containsKey(id)){
            callbacks.get(id).forEach(r -> {
                r.run();
            });
        }
//        callbacks.forEach((key, task) -> {
//            task.forEach(r -> {
//                try {
//                    r.run();
//                } catch (Exception e) {
//                    log.info("clear {} instance failed, error is {}", key, e.getMessage());
//                } finally {
//                    if (this.sceneIds.size() == 0 && this.status.get(Status.CLEARING) != null) {
//                        // 标识一下，清除完毕
//                        this.removeClearingStatus();
//                    }
//                }
//            });
//        });
    }

    public Boolean isClearing() {
        if (this.status.get(Status.CLEARING) == null) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean hasClear() {
        if (this.status.get(Status.HAS_CLEAR) == null) {
            return false;
        } else {
            return true;
        }
    }

    public void finishClearJob() {
        if (this.sceneIds.size() == 0 && this.status.get(Status.CLEARING) != null) {
            // 标识一下，清除完毕
            this.removeClearingStatus();
        }
    }

    public void removeSceneOrInstanceId(String id) {
        this.sceneIds.remove(id);
    }

    public void setStatus() {
        this.status.put(Status.CLEARING, Status.CLEARING_VALUE);
        this.status.put(Status.HAS_CLEAR, Status.HAS_CLEAR_VALUE);
    }

    public void startClear() {
        this.status.put(Status.CLEARING, Status.CLEARING_VALUE);
    }

    public void removeClearingStatus() {
        this.status.remove(Status.CLEARING);
    }

    public static class Status{
        public static final int CLEARING = 0;
        public static final String CLEARING_VALUE = "CLEARING";
        public static final int HAS_CLEAR = 1;
        public static final String HAS_CLEAR_VALUE = "HAS_CLEAR_VALUE";
    }

    public void clearAll() {
        this.status.clear();
        this.callbacks.clear();
        this.sceneIds.clear();
    }
}
