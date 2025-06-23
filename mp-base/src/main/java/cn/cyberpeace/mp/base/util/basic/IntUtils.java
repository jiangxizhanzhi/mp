 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name IntUtils.java.
 // File size 713.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.basic;

import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName IntUtils
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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
