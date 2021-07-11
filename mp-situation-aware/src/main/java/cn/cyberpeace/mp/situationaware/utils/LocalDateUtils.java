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


public class LocalDateUtils {

    private LocalDateUtils() {
    }

    
    public static List<String> fetchFormatterTime(LocalDateTime startTime, LocalDateTime endTime) {
        var formatter = DateTimeFormatter.ofPattern("yyMMdd");
        long betweenDay = ChronoUnit.DAYS.between(startTime, endTime);
        long month = ChronoUnit.MONTHS.between(startTime, endTime);
        int days = (int) betweenDay;
        if (month > 1) {
            return IntStream.range(0, days + 2).boxed().map(startTime::plusDays).map((time) -> time.format(DateTimeFormatter.ofPattern("yyMM*")))
                    .distinct().collect(Collectors.toList());
        }
        return IntStream.range(0, days + 2).boxed().map(startTime::plusDays).map((time) -> time.format(formatter))
                .collect(Collectors.toList());
    }

    
    public static String timeToString(Long time, String pattern) {
        Assert.notNull(time, "time is null");
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern(pattern);
        return ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }

    
    @Deprecated
    public static Long toEpochMilli(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }
}
