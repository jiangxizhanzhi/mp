 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CastUtils.java.
 // File size 823.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.util.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CastUtils {

    private CastUtils() {}

    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (obj instanceof List<?>) {
            ((List<?>) obj).forEach(o -> result.add(clazz.cast(o)));
        }

        return result;
    }

    public static <K, V> Map<K, V> castMap(Object obj, Class<K> kClass, Class<V> vClass) {
        Map<K, V> result = new HashMap<>();
        if (obj instanceof Map<?, ?>) {
            ((Map<?, ?>) obj).forEach((k, v) -> result.put(kClass.cast(k), vClass.cast(v)));
            return result;
        }

        return null;
    }
}
