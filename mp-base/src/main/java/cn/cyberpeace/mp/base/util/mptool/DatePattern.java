package cn.cyberpeace.mp.base.util.mptool;

import java.time.format.DateTimeFormatter;

public class DatePattern {
    public static final DateTimeFormatter NORM_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter UTC_SIMPLE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");


}
