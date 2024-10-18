package cn.cyberpeace.mp.base.util.mptool;

import java.util.*;

public class CollectionUtil {

    /**
     * 数组转为ArrayList
     * Params:
     * values – 数组
     * Type parameters:
     * <T> – 集合元素类型
     */
    public static <T> List<T> toList(T... values) {
        if (isEmpty(values)) {
            return new ArrayList<>();
        }
        List<T> arrayList = new ArrayList<>(values.length);
        Collections.addAll(arrayList, values);
        return arrayList;
    }


    /**
     * Map是否为非空
     * Params:
     * map – 集合
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    /**
     * 集合是否为非空
     * Params:
     * collection – 集合
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 集合是否为非空
     * Params:
     * collection – 集合
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * 反序给定List，会在原List基础上直接修改
     * Params:
     * list – 被反转的List
     * Type parameters:
     * <T> – 元素类型
     */
    public static <T> List<T> reverse(List<T> list) {
        Collections.reverse(list);
        return list;
    }

    public static <T> boolean isEmpty(T... values) {
        return values.length == 0;
    }

}
