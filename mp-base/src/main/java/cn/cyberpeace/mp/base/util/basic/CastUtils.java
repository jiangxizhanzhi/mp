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
