package cn.cyberpeace.mp.base.util.basic;

import java.util.Map;

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
