package cn.cyberpeace.mp.base.util;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
public class LocalDateTimeUtils {

    //yyyy-MM-dd HH:mm:ss
    public static DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static DateTimeFormatter esdateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS+0800");

    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    /**
     * localDateTimeToString
     * @param localDateTime
     * @param dateTimeFormatter
     * @return String
     */
    public static String localDateTimeToString(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
        return localDateTime.format(dateTimeFormatter);
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(),
            ZoneId.systemDefault());
    }

    /**
     * string转localDateTime
     * @param date：2021-09-14T03:38:59Z
     * @return
     */
    public static LocalDateTime stringToLocalDateTime(String date) {
        return LocalDateTime.parse(date, dateTimeFormatter);
    }

    public static LocalDateTime convertToLocalDateTime(String date){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return LocalDateTimeUtils.dateToLocalDateTime(df.parse(date));
        } catch (ParseException e) {
           log.error("convert to local date time error:{}" , e.getMessage(),e);
        }
        return null;
    }
}
