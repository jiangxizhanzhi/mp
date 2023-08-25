package cn.cyberpeace.mp.aggregation.common;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Description
 * @Author zhanglin
 * @Date 2023/5/22
 */
@Component
@Data
public class SystemTimeCommon {
    private LocalDateTime startTime;
}