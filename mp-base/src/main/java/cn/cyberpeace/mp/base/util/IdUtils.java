 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name IdUtils.java.
 // File size 433.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

public class IdUtils {

    private IdUtils() {}

    private final static Snowflake snowflake = IdUtil.createSnowflake(25, 26);

    public static long snowflakeId(){
        return snowflake.nextId();
    }

    public static String globalId(){
        return String.valueOf(snowflakeId());
    }

}
