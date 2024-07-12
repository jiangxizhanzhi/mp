 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MapUtils.java.
 // File size 611.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.basic;

import java.util.Map;

/**
 * @ClassName MapUtils
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class MapUtils {

    /**
     * MapUtils
     */
    private MapUtils() {}

    /**
     * setDefault
     *
     * @param map
     * @param key
     * @param defaultValue
     * @return
     */
    public static <T extends Map<K, V>, K, V> V setDefault(T map, K key, V defaultValue) {
        V value = map.getOrDefault(key, defaultValue);
        map.put(key, value);
        return value;
    }

}
