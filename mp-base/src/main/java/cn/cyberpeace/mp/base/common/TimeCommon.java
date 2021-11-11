package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class TimeCommon {

    public static DateTimeFormatter dateTimeFormatter;

    public static DateTimeFormatter dateFormatter;

    public static DateTimeFormatter timeFormatter;

    private TimeCommon(BaseAppConfig baseAppConfig) {
        TimeCommon.dateTimeFormatter = DateTimeFormatter.ofPattern(baseAppConfig.getDateTimePattern());
        TimeCommon.dateFormatter = DateTimeFormatter.ofPattern(baseAppConfig.getDatePattern());
        TimeCommon.timeFormatter = DateTimeFormatter.ofPattern(baseAppConfig.getTimePattern());
    }

    public static String localDateTimeToString(LocalDateTime localDateTime) {
        return TimeUtils.localDateTimeToString(localDateTime, dateTimeFormatter);
    }

    public static String localDateToString(LocalDate localDate) {
        return TimeUtils.localDateToString(localDate, dateFormatter);
    }

    public static String localTimeToString(LocalTime localTime) {
        return TimeUtils.localTimeToString(localTime, timeFormatter);
    }

    public static LocalDateTime stringToLocalDateTime(String localDateTimeStr) {
        return TimeUtils.stringToLocalDateTime(localDateTimeStr, dateTimeFormatter);
    }

    public static LocalDate stringToLocalDate(String localDateStr) {
        return TimeUtils.stringToLocalDate(localDateStr, dateFormatter);
    }

    public static LocalTime stringToLocalTime(String localTimeStr) {
        return TimeUtils.stringToLocalTime(localTimeStr, timeFormatter);
    }

    public static String utcStringToLocalDateTimeString(String utcDateTimeStr) {
        return localDateTimeToString(TimeUtils.utcStringToLocalDateTime(utcDateTimeStr));
    }

    public static String localDateTimeStringToUtcString(String localDateTimeStr) {
        return TimeUtils.localDateTimeToUtcString(stringToLocalDateTime(localDateTimeStr));
    }

}
