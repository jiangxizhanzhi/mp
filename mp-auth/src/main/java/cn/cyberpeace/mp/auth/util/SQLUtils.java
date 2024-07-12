 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SQLUtils.java.
 // File size 521.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.util;

/**
 * @ClassName SQLUtils
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/10/13 09:15
 * @Version 1.0.0
 */
public class SQLUtils {

    /**
     * escapeLike
     *
     * @param value
     * @return
     */
    public static String escapeLike(String value) {
        if (value != null) {
            return value
                    .replaceAll("_", "\\\\_")
                    .replaceAll("%", "\\\\%");
        }
        return null;
    }


}
