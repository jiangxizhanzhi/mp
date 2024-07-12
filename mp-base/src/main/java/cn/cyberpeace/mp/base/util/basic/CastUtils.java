 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CastUtils.java.
 // File size 1373.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CastUtils
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class CastUtils {

    /**
     * CastUtils
     */
    private CastUtils() {}

    /**
     * castList
     *
     * @param obj
     * @param clazz
     * @return
     */
    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (obj instanceof List<?>) {
            for (Object o : ((List<?>) obj)) {
                result.add(clazz.cast(o));
            }
        }

        return result;
    }

    /**
     * castMap
     *
     * @param obj
     * @param kClass
     * @param vClass
     * @return
     */
    public static <K, V> Map<K, V> castMap(Object obj, Class<K> kClass, Class<V> vClass) {
        Map<K, V> result = new HashMap<>();
        if (obj instanceof Map<?, ?>) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
                Object k = entry.getKey();
                Object v = entry.getValue();
                result.put(kClass.cast(k), vClass.cast(v));
            }
            return result;
        }

        return null;
    }
}
