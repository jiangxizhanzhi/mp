package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/6 13:51
 */
@Slf4j
public class SegmentCount2IntervalSecondsUtil {


    /**
     * 按照给定分段数平均切分时间段，并计算分段之后的秒数
     * @param start
     * @param end
     * @param segmentCount
     * @return
     */
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
        Long seconds = duration.toSeconds();
        // 时间间隔(segmentCount -1)，为确保余数，再加上1
        Long intervalSeconds = seconds / (segmentCount - 1) + 1;

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

    public static List<LocalDateTime> generateTimeList(LocalDateTime start, LocalDateTime end, long intervalSeconds) {
        List<LocalDateTime> timeList = new ArrayList<>();
        while (start.isBefore(end)) {
            timeList.add(start);
            start = start.plusSeconds(intervalSeconds);
        }
        timeList.add(end);
        return timeList;
    }

}
