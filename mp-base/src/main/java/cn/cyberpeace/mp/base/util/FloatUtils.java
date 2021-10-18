package cn.cyberpeace.mp.base.util;

public class FloatUtils {

    private FloatUtils() {}

    public static Float zeroNull(Float value) {
        return (value == null || value.equals(0.0f)) ? null : value;
    }

    public static Float fromStr(String str) {
        return str != null ? Float.parseFloat(str) : 0;
    }

}
