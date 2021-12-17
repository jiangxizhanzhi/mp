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
