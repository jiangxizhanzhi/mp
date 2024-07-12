 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TimeCommon.java.
 // File size 3259.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @ClassName TimeCommon
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Component
@Slf4j
public class TimeCommon {

    public static DateTimeFormatter dateTimeFormatter;

    public static DateTimeFormatter dateFormatter;

    public static DateTimeFormatter timeFormatter;

    /**
     * TimeCommon
     *
     * @param baseAppConfig
     */
    private TimeCommon(BaseAppConfig baseAppConfig) {
        TimeCommon.dateTimeFormatter = DateTimeFormatter.ofPattern(baseAppConfig.getDateTimePattern());
        TimeCommon.dateFormatter = DateTimeFormatter.ofPattern(baseAppConfig.getDatePattern());
        TimeCommon.timeFormatter = DateTimeFormatter.ofPattern(baseAppConfig.getTimePattern());
    }

    /**
     * localDateTimeToString
     *
     * @param localDateTime
     * @return
     */
    public static String localDateTimeToString(LocalDateTime localDateTime) {
        return TimeUtils.localDateTimeToString(localDateTime, dateTimeFormatter);
    }

    /**
     * localDateToString
     *
     * @param localDate
     * @return
     */
    public static String localDateToString(LocalDate localDate) {
        return TimeUtils.localDateToString(localDate, dateFormatter);
    }

    /**
     * localTimeToString
     *
     * @param localTime
     * @return
     */
    public static String localTimeToString(LocalTime localTime) {
        return TimeUtils.localTimeToString(localTime, timeFormatter);
    }

    /**
     * stringToLocalDateTime
     *
     * @param localDateTimeStr
     * @return
     */
    public static LocalDateTime stringToLocalDateTime(String localDateTimeStr) {
        return TimeUtils.stringToLocalDateTime(localDateTimeStr, dateTimeFormatter);
    }

    /**
     * stringToLocalDate
     *
     * @param localDateStr
     * @return
     */
    public static LocalDate stringToLocalDate(String localDateStr) {
        return TimeUtils.stringToLocalDate(localDateStr, dateFormatter);
    }

    /**
     * stringToLocalTime
     *
     * @param localTimeStr
     * @return
     */
    public static LocalTime stringToLocalTime(String localTimeStr) {
        return TimeUtils.stringToLocalTime(localTimeStr, timeFormatter);
    }

    /**
     * utcStringToLocalDateTimeString
     *
     * @param utcDateTimeStr
     * @return
     */
    public static String utcStringToLocalDateTimeString(String utcDateTimeStr) {
        return localDateTimeToString(TimeUtils.utcStringToLocalDateTime(utcDateTimeStr));
    }

    /**
     * localDateTimeStringToUtcString
     *
     * @param localDateTimeStr
     * @return
     */
    public static String localDateTimeStringToUtcString(String localDateTimeStr) {
        return TimeUtils.localDateTimeToUtcString(stringToLocalDateTime(localDateTimeStr));
    }

}
