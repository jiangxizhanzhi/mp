 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SQLUtils.java.
 // File size 399.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.util;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/10/13 09:15
 */
public class SQLUtils {

    public static String escapeLike(String value) {
        if (value != null) {
            return value
                    .replaceAll("_", "\\\\_")
                    .replaceAll("%", "\\\\%");
        }
        return null;
    }


}
