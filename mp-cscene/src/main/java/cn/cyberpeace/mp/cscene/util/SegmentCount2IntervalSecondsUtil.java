package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/6 13:51
 */
public class SegmentCount2IntervalSecondsUtil {


    public static Long calculateIntervalSeconds(LocalDateTime start, LocalDateTime end, int segmentCount) {
        if (segmentCount < 1) {
            throw new MpRuntimeException("分段数不能小于1");
        }

        if (end.isBefore(start)) {
            throw new MpRuntimeException("结束时间不可早于开始时间");
        }

        Duration duration = Duration.between(start, end);
        Long seconds = duration.toSeconds();
        // 时间间隔(segmentCount -1)
        Long intervalSeconds = seconds / (segmentCount - 1);

        /*List<LocalDateTime> timeSegs = new ArrayList<>();
        for (int i = 0; i < segmentCount - 1; i++) {
            timeSegs.add(start);
            start = start.plusSeconds(intervalSeconds);
        }

        timeSegs.add(end);

        for (int i = 0; i < timeSegs.size(); i++) {
            log.info(" i:{}, intervalSeconds:{}, timeSegs:{}", i, intervalSeconds, timeSegs.get(i));
        }*/

        return intervalSeconds;
    }

}
