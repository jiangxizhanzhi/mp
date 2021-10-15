package cn.cyberpeace.mp.base.util;

import org.apache.commons.lang3.StringUtils;

public class IntUtils {

    private IntUtils() {}

    public static Integer zeroNull(Integer value) {
        return (value == null || value.equals(0)) ? null : value;
    }

    public static Integer fromStr(String str) {
        return StringUtils.isNumeric(str) ? Integer.parseInt(str) : 0;
    }

}
