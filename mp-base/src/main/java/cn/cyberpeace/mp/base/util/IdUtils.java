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
