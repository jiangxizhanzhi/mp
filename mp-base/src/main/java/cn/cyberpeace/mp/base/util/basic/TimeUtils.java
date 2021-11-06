package cn.cyberpeace.mp.base.util.basic;

import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
public class TimeUtils {

    public static final DateTimeFormatter tzDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static final TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

    public static final ZoneId utcZoneId = utcTimeZone.toZoneId();

    public static final DateTimeFormatter utcDateTimeFormatter = tzDateTimeFormatter.withZone(utcZoneId);

    private TimeUtils() {}

    public static LocalDateTime utcStringToLocalDateTime(String utcDateTimeStr) {
        var zonedDatetime = ZonedDateTime.parse(utcDateTimeStr, utcDateTimeFormatter);
        return zonedDatetime.withZoneSameInstant(TimeZone.getDefault().toZoneId()).toLocalDateTime();
    }

    public static String localDateTimeToUtcString(LocalDateTime localDateTime) {
        var zonedDatetime = localDateTime.atZone(TimeZone.getDefault().toZoneId());
        return zonedDatetime.format(utcDateTimeFormatter);
    }

    public static String localDateTimeToString(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
        return localDateTime.format(dateTimeFormatter);
    }

    public static String localDateToString(LocalDate localDate, DateTimeFormatter dateTimeFormatter) {
        return localDate.format(dateTimeFormatter);
    }

    public static String localTimeToString(LocalTime localTime, DateTimeFormatter dateTimeFormatter) {
        return localTime.format(dateTimeFormatter);
    }

    public static LocalDateTime stringToLocalDateTime(String localDateTimeStr, DateTimeFormatter dateTimeFormatter) {
        return LocalDateTime.parse(localDateTimeStr, dateTimeFormatter);
    }

    public static LocalDate stringToLocalDate(String localDateStr, DateTimeFormatter dateTimeFormatter) {
        return LocalDate.parse(localDateStr, dateTimeFormatter);
    }

    public static LocalTime stringToLocalTime(String localTimeStr, DateTimeFormatter dateTimeFormatter) {
        return LocalTime.parse(localTimeStr, dateTimeFormatter);
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}
