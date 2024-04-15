package cn.cyberpeace.mp.situationaware.utils;

import org.springframework.util.Assert;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        long betweenDay = ChronoUnit.DAYS.between(startTime, endTime);
        long month = ChronoUnit.MONTHS.between(startTime, endTime);
        int days = (int) betweenDay;
        if (month > 1) {
            List<String> list = new ArrayList<>();
            Set<String> uniqueValues = new HashSet<>();
            int bound = days + 2;
            for (int i = 0; i < bound; i++) {
                Integer integer = i;
                LocalDateTime time = startTime.plusDays(integer);
                String format = time.format(DateTimeFormatter.ofPattern("yyMM*"));
                if (uniqueValues.add(format)) {
                    list.add(format);
                }
            }
            return list;
        }
        List<String> list = new ArrayList<>();
        int bound = days + 2;
        for (int i = 0; i < bound; i++) {
            Integer integer = i;
            LocalDateTime time = startTime.plusDays(integer);
            String format = time.format(formatter);
            list.add(format);
        }
        return list;
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
    @Deprecated
    public static Long toEpochMilli(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }
}
