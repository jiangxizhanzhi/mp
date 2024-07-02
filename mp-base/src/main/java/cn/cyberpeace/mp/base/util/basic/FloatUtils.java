package cn.cyberpeace.mp.base.util.basic;

/**
 * @ClassName FloatUtils
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class FloatUtils {

    /**
     * FloatUtils
     */
    private FloatUtils() {}

    /**
     * zeroNull
     *
     * @param value
     * @return
     */
    public static Float zeroNull(Float value) {
        return (value == null || value.equals(0.0f)) ? null : value;
    }

    /**
     * fromStr
     *
     * @param str
     * @return
     */
    public static Float fromStr(String str) {
        return str != null ? Float.parseFloat(str) : 0;
    }

}
