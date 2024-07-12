 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BoolUtils.java.
 // File size 430.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
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

