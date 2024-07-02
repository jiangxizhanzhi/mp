package cn.cyberpeace.mp.base.util.basic;

public class BoolUtils {

    /**
     * BoolUtils
     */
    private BoolUtils() {}

    /**
     * bool
     *
     * @param o
     * @return boolean
     */
    public static boolean bool(Object o) {
        return o != null && (boolean) o;
    }

}

