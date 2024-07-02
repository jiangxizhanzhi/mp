package cn.cyberpeace.mp.base.util.mptool;

import java.util.Collection;
import java.util.List;

public class CollUtil {

    /**
     * toList
     *
     * @param values
     * @return
     */
    public static <T> List<T> toList(T... values) {
        return CollectionUtil.toList(values);
    }

    /**
     * isEmpty
     *
     * @param collection
     * @return boolean
     */
    public static boolean isEmpty(Collection<?> collection) {
        return CollectionUtil.isEmpty(collection);
    }

    /**
     * isNotEmpty
     *
     * @param collection
     * @return boolean
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return CollectionUtil.isNotEmpty(collection);
    }
}
