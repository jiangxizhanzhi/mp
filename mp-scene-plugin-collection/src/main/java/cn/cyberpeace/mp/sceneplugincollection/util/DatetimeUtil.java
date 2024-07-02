package cn.cyberpeace.mp.sceneplugincollection.util;

import cn.cyberpeace.mp.base.util.mptool.DatePattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @ClassName DatetimeUtil
 * @Description
 * @Author zhouxinghuai
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public class DatetimeUtil {


    /**
     * 2022-06-16T00:59:54.188485Z -> 2022-06-16T00:59:54
     * @param datetime
     * @return
     */
    public static LocalDateTime parseDateTimeFronLacunchOutput(String datetime) {
        String date = StringUtils.substringBeforeLast(datetime, ".");
        DateTimeFormatter df = DatePattern.UTC_SIMPLE_FORMAT;
        LocalDateTime dateTime = LocalDateTime.parse(date, df);
        return dateTime;
    }

}
