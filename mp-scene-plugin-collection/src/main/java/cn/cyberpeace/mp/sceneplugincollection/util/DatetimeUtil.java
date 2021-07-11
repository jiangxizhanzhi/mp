package cn.cyberpeace.mp.sceneplugincollection.util;

import cn.hutool.core.date.DatePattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Slf4j
public class DatetimeUtil {


    
    public static LocalDateTime parseDateTimeFronLacunchOutput(String datetime) {
        String date = StringUtils.substringBeforeLast(datetime, ".");
        DateTimeFormatter df = DatePattern.UTC_SIMPLE_FORMAT.getDateTimeFormatter();
        LocalDateTime dateTime = LocalDateTime.parse(date, df);
        return dateTime;
    }

}
