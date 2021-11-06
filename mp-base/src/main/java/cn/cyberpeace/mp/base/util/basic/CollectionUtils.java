package cn.cyberpeace.mp.base.util.basic;

import java.util.Collection;

public class CollectionUtils {

    private CollectionUtils() {}

    public static <T extends Collection<?>> T emptyNull(T value) {
        return (value == null || value.isEmpty()) ? null : value;
    }

}
