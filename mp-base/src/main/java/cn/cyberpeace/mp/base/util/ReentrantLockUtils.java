 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ReentrantLockUtils.java.
 // File size 1020.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName ReentrantLockUtils
 * @Description
 * @Author maojun
 * @Date 2022/2/16
 * @Version 1.0.0
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

    /**
     * unLock
     *
     * @param key
     */
    public synchronized static void unLock(String key) {
        ReentrantLock lock = LOCKS.remove(key);
        if (lock != null) {
            lock.unlock();
        }
    }
}
