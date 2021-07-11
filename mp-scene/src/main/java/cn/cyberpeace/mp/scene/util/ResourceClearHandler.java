package cn.cyberpeace.mp.scene.util;

import cn.hutool.core.collection.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


@Component
@Slf4j
public class ResourceClearHandler {
    private Set<String> sceneIds = new ConcurrentHashSet<>();
    private Map<String, List<Runnable>> callbacks = new ConcurrentHashMap<>();
    private Map<Integer, String> status = new ConcurrentHashMap<>();

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
            
            this.removeClearingStatus();
        }
    }

    public void removeSceneOrInstanceId(String id) {
        this.sceneIds.remove(id);
    }

    public void setClearingAndHasClearStatus() {
        this.status.put(Status.CLEARING, Status.CLEARING_VALUE);
        this.status.put(Status.HAS_CLEAR, Status.HAS_CLEAR_VALUE);
    }

    public void setClearingStatus() {
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
