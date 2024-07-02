package cn.cyberpeace.mp.base.util.mptool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    /**
     * format
     *
     * @param localDateTime
     * @param format
     * @return
     */
    public static String format(LocalDateTime localDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return localDateTime.format(formatter);
    }


}
