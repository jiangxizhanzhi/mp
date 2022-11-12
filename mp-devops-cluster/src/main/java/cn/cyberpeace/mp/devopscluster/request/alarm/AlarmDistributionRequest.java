package cn.cyberpeace.mp.devopscluster.request.alarm;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @Description
 * @Author shgang
 * @Date 2022/11/12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmDistributionRequest {
    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    public void ensure() {
        LocalDateTime now = LocalDateTime.now();

        if (Objects.isNull(getEndTime())) {
            setEndTime(now);
        }

        if (Objects.isNull(getStartTime())) {
            setStartTime(getEndTime().minusDays(7));
        }
    }
}
