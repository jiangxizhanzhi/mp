package cn.cyberpeace.mp.base.util.mptool;

import java.time.format.DateTimeFormatter;

public class DatePattern {

    /**
     * 标准日期时间格式
     */
    public static final DateTimeFormatter NORM_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * UTC时间
     */
    public static final DateTimeFormatter UTC_SIMPLE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");


}
