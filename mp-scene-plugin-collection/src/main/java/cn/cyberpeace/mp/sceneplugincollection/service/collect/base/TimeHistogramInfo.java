package cn.cyberpeace.mp.sceneplugincollection.service.collect.base;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TimeHistogramInfo {
    private String time;
    private long count;
    private LocalDateTime localDateTime;

    public TimeHistogramInfo(String time, long count, LocalDateTime localDateTime) {
        this.time = time;
        this.count = count;
        this.localDateTime = localDateTime;
    }
}
