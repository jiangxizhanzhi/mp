package cn.cyberpeace.mp.base.util;

import org.apache.commons.lang3.StringUtils;

public class FloatUtils {

    private FloatUtils() {}

    public static Float zeroNull(Float value) {
        return (value == null || value.equals(0.0f)) ? null : value;
    }

    public static Float fromStr(String str) {
        return StringUtils.isNumeric(str) ? Float.parseFloat(str) : 0;
    }

}
