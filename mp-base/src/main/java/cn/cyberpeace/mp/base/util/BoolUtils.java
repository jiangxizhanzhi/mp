package cn.cyberpeace.mp.base.util;

public class BoolUtils {

    private BoolUtils() {}

    public static boolean bool(Object o) {
        return o != null && (boolean) o;
    }

}

