 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BoolUtils.java.
 // File size 203.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.util.basic;

public class BoolUtils {

    private BoolUtils() {}

    public static boolean bool(Object o) {
        return o != null && (boolean) o;
    }

}

