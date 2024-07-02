package cn.cyberpeace.mp.sceneplugincollection.service.collect.base;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @ClassName TimeHistogramInfo
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Getter
public class TimeHistogramInfo {
    private String time;
    private long count;
    private LocalDateTime localDateTime;

    /**
     * TimeHistogramInfo
     *
     * @param time
     * @param count
     * @param localDateTime
     */
    public TimeHistogramInfo(String time, long count, LocalDateTime localDateTime) {
        this.time = time;
        this.count = count;
        this.localDateTime = localDateTime;
    }
}
