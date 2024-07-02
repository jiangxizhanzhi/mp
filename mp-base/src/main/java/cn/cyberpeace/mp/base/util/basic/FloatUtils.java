package cn.cyberpeace.mp.base.util.basic;

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
