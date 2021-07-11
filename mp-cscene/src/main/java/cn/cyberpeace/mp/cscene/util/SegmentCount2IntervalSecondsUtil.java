package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class SegmentCount2IntervalSecondsUtil {


    
    public static Long calculateIntervalSeconds(LocalDateTime start, LocalDateTime end, int segmentCount) {
        if (segmentCount < 1) {
            log.error("segmentCount should not less than 1! {}", segmentCount);
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID, "分段数不能小于1");
        }

        if (end.isBefore(start)) {
            log.error("end time {} should not before than start time {}!", end, start);
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID, "结束时间不可早于开始时间");
        }

        Duration duration = Duration.between(start, end);
        Long millis = duration.toMillis();
        
        Long intervalMilis = millis / (segmentCount - 1);
        Long intervalSeconds = intervalMilis / 1000;

        

        if (intervalSeconds == 0) {
            return 1L;
        }
        return intervalSeconds;
    }

    public static List<LocalDateTime> generateTimeList(LocalDateTime start, LocalDateTime end, long intervalSeconds) {
        List<LocalDateTime> timeList = new ArrayList<>();
        while (!start.isAfter(end)) {
            timeList.add(start);
            start = start.plusSeconds(intervalSeconds);
        }
        return timeList;
    }

}
