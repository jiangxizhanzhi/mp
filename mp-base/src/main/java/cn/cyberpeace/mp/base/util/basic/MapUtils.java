 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MapUtils.java.
 // File size 341.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.util.basic;

import java.util.Map;

public class MapUtils {

    private MapUtils() {}

    public static <T extends Map<K, V>, K, V> V setDefault(T map, K key, V defaultValue) {
        var value = map.getOrDefault(key, defaultValue);
        map.put(key, value);
        return value;
    }

}
