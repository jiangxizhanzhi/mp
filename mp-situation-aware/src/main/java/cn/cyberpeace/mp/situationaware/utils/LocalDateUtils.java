package cn.cyberpeace.mp.situationaware.utils;

import org.springframework.util.Assert;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * LocalDate工具类
 * @author wanggang
 */
public class LocalDateUtils {

    private LocalDateUtils() {
    }

    /**
     * 获取比如2021-05-01 到2021-05-20.所有的日期，方便构建所有的时间字符串210501,210502,....
     * @param startTime startTime
     * @param endTime endTime
     * @return List<String>
     */
    public static List<String> fetchFormatterTime(LocalDateTime startTime, LocalDateTime endTime) {
        var formatter = DateTimeFormatter.ofPattern("yyMMdd");
        long betweenDay = ChronoUnit.DAYS.between(startTime, endTime);
        long month = ChronoUnit.MONTHS.between(startTime, endTime);
        int days = (int) betweenDay;
        if (month > 1) {
            return IntStream.range(0, days + 2).boxed().map(startTime::plusDays).map((time) -> time.format(DateTimeFormatter.ofPattern("yyMM*")))
                    .distinct().collect(Collectors.toList());
        }
        return IntStream.range(0, days + 1).boxed().map(startTime::plusDays).map((time) -> time.format(formatter))
                .collect(Collectors.toList());
    }

    /**
     * 时间戳转字符串
     * @param time
     * @param pattern
     * @return
     */
    public static String timeToString(Long time, String pattern) {
        Assert.notNull(time, "time is null");
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern(pattern);
        return ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }

    /**
     * LocalDateTime转换成毫秒
     * @param dateTime
     * @return
     */
    public static Long toEpochMilli(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * LocalDateTime转换成秒
     * @param dateTime
     * @return
     */
    public static Long toEpochSecond(LocalDateTime dateTime) {
        return toEpochMilli(dateTime) / 1000;
    }
}
