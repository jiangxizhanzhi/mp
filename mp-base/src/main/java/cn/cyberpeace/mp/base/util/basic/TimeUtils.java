 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TimeUtils.java.
 // File size 7674.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.basic;

 import cn.cyberpeace.mp.base.exception.MpRuntimeException;
 import lombok.extern.slf4j.Slf4j;
 import org.apache.commons.lang3.StringUtils;

 import java.sql.Timestamp;
 import java.time.*;
 import java.time.format.DateTimeFormatter;
 import java.time.format.DateTimeParseException;
 import java.util.Date;
 import java.util.TimeZone;

/**
 * @ClassName TimeUtils
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public class TimeUtils {

    private static final DateTimeFormatter yyyyMMddHHmm_Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static final DateTimeFormatter tzDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static final DateTimeFormatter tzMillsDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static final DateTimeFormatter tz6MillsDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");

    public static final TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

    public static final ZoneId utcZoneId = utcTimeZone.toZoneId();

    public static final DateTimeFormatter utcDateTimeFormatter = tzDateTimeFormatter.withZone(utcZoneId);

    /**
     * TimeUtils
     */
    private TimeUtils() {}

    /**
     * localDateTimeToString
     *
     * @param localDateTime
     * @return
     */
    public static String localDateTimeToString(LocalDateTime localDateTime) {
        return localDateTimeToString(localDateTime, dateTimeFormatter);
    }

    /**
     * localDateToString
     *
     * @param localDate
     * @return
     */
    public static String localDateToString(LocalDate localDate) {
        return localDateToString(localDate, dateFormatter);
    }

    /**
     * localTimeToString
     *
     * @param localTime
     * @return
     */
    public static String localTimeToString(LocalTime localTime) {
        return localTimeToString(localTime, timeFormatter);
    }

    /**
     * stringToLocalDateTime
     *
     * @param localDateTimeStr
     * @return
     */
    public static LocalDateTime stringToLocalDateTime(String localDateTimeStr) {
        return stringToLocalDateTime(localDateTimeStr, dateTimeFormatter);
    }

    /**
     * stringToLocalDate
     *
     * @param localDateStr
     * @return
     */
    public static LocalDate stringToLocalDate(String localDateStr) {
        return stringToLocalDate(localDateStr, dateFormatter);
    }

    /**
     * stringToLocalTime
     *
     * @param localTimeStr
     * @return
     */
    public static LocalTime stringToLocalTime(String localTimeStr) {
        return stringToLocalTime(localTimeStr, timeFormatter);
    }

    /**
     * utcStringToLocalDateTimeString
     *
     * @param utcDateTimeStr
     * @return
     */
    public static String utcStringToLocalDateTimeString(String utcDateTimeStr) {
        return localDateTimeToString(utcStringToLocalDateTime(utcDateTimeStr));
    }

    /**
     * localDateTimeStringToUtcString
     *
     * @param localDateTimeStr
     * @return
     */
    public static String localDateTimeStringToUtcString(String localDateTimeStr) {
        return localDateTimeToUtcString(stringToLocalDateTime(localDateTimeStr));
    }

    /**
     * utcStringToLocalDateTime
     *
     * @param utcDateTimeStr
     * @return
     */
    public static LocalDateTime utcStringToLocalDateTime(String utcDateTimeStr) {
        ZonedDateTime zonedDatetime = ZonedDateTime.parse(utcDateTimeStr, utcDateTimeFormatter);
        return zonedDatetime.withZoneSameInstant(TimeZone.getDefault().toZoneId()).toLocalDateTime();
    }

    /**
     * localDateTimeToUtcString
     *
     * @param localDateTime
     * @return
     */
    public static String localDateTimeToUtcString(LocalDateTime localDateTime) {
        ZonedDateTime zonedDatetime = localDateTime.atZone(TimeZone.getDefault().toZoneId());
        return zonedDatetime.format(utcDateTimeFormatter);
    }

    /**
     * localDateTimeToString
     *
     * @param localDateTime
     * @param dateTimeFormatter
     * @return
     */
    public static String localDateTimeToString(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
        return localDateTime.format(dateTimeFormatter);
    }

    /**
     * localDateToString
     *
     * @param localDate
     * @param dateTimeFormatter
     * @return
     */
    public static String localDateToString(LocalDate localDate, DateTimeFormatter dateTimeFormatter) {
        return localDate.format(dateTimeFormatter);
    }

    /**
     * localTimeToString
     *
     * @param localTime
     * @param dateTimeFormatter
     * @return
     */
    public static String localTimeToString(LocalTime localTime, DateTimeFormatter dateTimeFormatter) {
        return localTime.format(dateTimeFormatter);
    }

    /**
     * localTime2yyyyMMddHHmm
     *
     * @param localTime
     * @return
     */
    public static String localTime2yyyyMMddHHmm(LocalDateTime localTime) {
        return localTime.format(yyyyMMddHHmm_Formatter);
    }

    /**
     * yyyyMMddHHmm2LocalDate
     *
     * @param localDateStr
     * @return
     */
    public static LocalDateTime yyyyMMddHHmm2LocalDate(String localDateStr) {
        return LocalDateTime.parse(localDateStr, yyyyMMddHHmm_Formatter);
    }

    /**
     * stringToLocalDateTime
     *
     * @param localDateTimeStr
     * @param dateTimeFormatter
     * @return
     */
    public static LocalDateTime stringToLocalDateTime(String localDateTimeStr, DateTimeFormatter dateTimeFormatter) {
        return LocalDateTime.parse(localDateTimeStr, dateTimeFormatter);
    }

    /**
     * stringToLocalDate
     *
     * @param localDateStr
     * @param dateTimeFormatter
     * @return
     */
    public static LocalDate stringToLocalDate(String localDateStr, DateTimeFormatter dateTimeFormatter) {
        return LocalDate.parse(localDateStr, dateTimeFormatter);
    }

    /**
     * stringToLocalTime
     *
     * @param localTimeStr
     * @param dateTimeFormatter
     * @return
     */
    public static LocalTime stringToLocalTime(String localTimeStr, DateTimeFormatter dateTimeFormatter) {
        return LocalTime.parse(localTimeStr, dateTimeFormatter);
    }

    /**
     * dateToLocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * timestampToLocalDateTime
     *
     * @param timestamp
     * @return
     */
    public static LocalDateTime timestampToLocalDateTime(Long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    /**
     * utc时间转localDateTime（不转时区）
     * @param utcTime
     * @return
     */
    public static LocalDateTime utcToLocalDateTime(String utcTime) {
        LocalDateTime time;
        if (utcTime.contains("T")) {
            OffsetDateTime dateTime = OffsetDateTime.parse(utcTime);
            time = dateTime.toLocalDateTime();
        } else {
            time = stringToLocalDateTime(utcTime);
        }
        return time;
    }

    /**
     * 获取 LocalTime 的下一个 Date
     *
     * @param localTime 不带日期的时间
     * @return 下一次该时间的日期+时间
     */
    public static Date getNextDate(LocalTime localTime) {
        LocalTime now = LocalTime.now();
        LocalDate localDate = LocalDate.now();
        // 当前时间已经晚于传入的时间
        if (localTime.isBefore(now)) {
            // 获取第二天的该时间点
            localDate = localDate.plusDays(1L);
        }
        Timestamp timestamp = Timestamp.valueOf(localDate.atTime(localTime));
        return new Date(timestamp.getTime());
    }

    public static String format(LocalTime localTime) {
        if (localTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return localTime.format(formatter);
    }

    public static LocalTime toLocalTime(String time) {
        if (StringUtils.isEmpty(time)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        try {
            return LocalTime.parse(time, formatter);
        }catch (DateTimeParseException e) {
            System.out.println("Error parsing time: " + e.getMessage());
            throw new MpRuntimeException("时间[" + time + "]格式化错误");
        }
    }
}
