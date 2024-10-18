package cn.cyberpeace.mp.base.util.mptool;

import java.util.Collection;
import java.util.List;

public class CollUtil {

    public static <T> List<T> toList(T... values) {
        return CollectionUtil.toList(values);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return CollectionUtil.isEmpty(collection);
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return CollectionUtil.isNotEmpty(collection);
    }
}
