 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RandomUtils.java.
 // File size 1162.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.basic;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @ClassName RandomUtils
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class RandomUtils {

    private static final Random RANDOM = new Random();

    /**
     * RandomUtils
     */
    private RandomUtils() {}

    /**
     * sample
     *
     * @param pool
     * @param count
     * @return
     */
    public static <T> List<T> sample(List<T> pool, int count) {
        int poolSize = pool.size();
        if (poolSize < count) {
            throw new MpRuntimeException("No enough resource for random pick");
        }

        List<T> tmpPool = new ArrayList<>(pool);
        if (poolSize == count) {
            return tmpPool;
        }

        List<T> sample = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int nextIndex = RANDOM.nextInt(tmpPool.size());
            sample.add(tmpPool.remove(nextIndex));
        }

        return sample;
    }

}
