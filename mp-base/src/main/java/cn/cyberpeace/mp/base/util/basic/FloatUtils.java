 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FloatUtils.java.
 // File size 349.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.util.basic;

public class FloatUtils {

    private FloatUtils() {}

    public static Float zeroNull(Float value) {
        return (value == null || value.equals(0.0f)) ? null : value;
    }

    public static Float fromStr(String str) {
        return str != null ? Float.parseFloat(str) : 0;
    }

}
