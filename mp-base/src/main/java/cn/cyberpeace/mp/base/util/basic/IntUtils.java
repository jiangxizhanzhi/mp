 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name IntUtils.java.
 // File size 411.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.util.basic;

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
