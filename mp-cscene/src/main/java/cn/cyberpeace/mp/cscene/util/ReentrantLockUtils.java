package cn.cyberpeace.mp.cscene.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


public final class ReentrantLockUtils {

    private static Map<String, ReentrantLock> LOCKS = new ConcurrentHashMap<>();

    
    public synchronized static boolean tryLock(String key) {
        if (!LOCKS.containsKey(key)) {
            LOCKS.put(key, new ReentrantLock());
        }
        return LOCKS.get(key).tryLock();
    }

    public synchronized static void unLock(String key) {
        ReentrantLock lock = LOCKS.remove(key);
        if (lock != null) {
            lock.unlock();
        }
    }
}
