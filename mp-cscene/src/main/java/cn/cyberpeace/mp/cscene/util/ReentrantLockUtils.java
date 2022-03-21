package cn.cyberpeace.mp.cscene.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description
 * @Author maojun
 * @Date 2022/2/16
 */
public final class ReentrantLockUtils {

    private static Map<String, ReentrantLock> LOCKS = new ConcurrentHashMap<>();

    /**
     * tryLock 和 unLock得互斥，不然一个线程lock一个线程unlock可能会出现问题
     * @param key
     * @return
     */
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
