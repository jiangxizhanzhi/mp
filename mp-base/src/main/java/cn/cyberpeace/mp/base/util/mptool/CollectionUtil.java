package cn.cyberpeace.mp.base.util.mptool;

import java.util.*;

public class CollectionUtil {

    public static <T> List<T> toList(T... values) {
        List<T> list = new ArrayList<>();
        for (T value : values) {
            list.add(value);
        }
        return list;
    }


    public static boolean isNotEmpty(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    public static <T> List<T> reverse(List<T> list) {
        Collections.reverse(list);
        return list;
    }

}
