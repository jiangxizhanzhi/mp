package cn.cyberpeace.mp.base.util.basic;

import org.apache.commons.lang3.StringUtils;

public class IntUtils {

    /**
     * IntUtils
     */
    private IntUtils() {}

    /**
     * zeroNull
     *
     * @param value
     * @return
     */
    public static Integer zeroNull(Integer value) {
        return (value == null || value.equals(0)) ? null : value;
    }

    /**
     * fromStr
     *
     * @param str
     * @return
     */
    public static Integer fromStr(String str) {
        return StringUtils.isNumeric(str) ? Integer.parseInt(str) : 0;
    }

}
