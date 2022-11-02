package cn.cyberpeace.mp.flow.v2.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description
 * @Author maojun
 * @Date 2022/11/1
 */
@Data
public class FlowTaskCountQueryBO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
