package cn.cyberpeace.mp.base.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeUtils {

    //yyyy-MM-dd HH:mm:ss
    public static DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * localDateTimeToString
     * @param localDateTime
     * @param dateTimeFormatter
     * @return String
     */
    public static String localDateTimeToString(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
        return localDateTime.format(dateTimeFormatter);
    }
}
