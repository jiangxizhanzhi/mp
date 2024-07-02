package cn.cyberpeace.mp.base.util.basic;

/**
 * @ClassName BoolUtils
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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

