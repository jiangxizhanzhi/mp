package cn.cyberpeace.mp.devops.cluster.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @Description: 告警数量统计信息
 * @Author qiaokang
 * @Date：2022/7/19 19:49
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "告警数量统计信息")
public class AlarmStatisticsVO {

    /**
     * 告警id
     */
    @ApiModelProperty(value = "告警id")
    private String id;

    /**
     * 告警数量
     */
    @ApiModelProperty(value = "告警数量")
    private int alarmCount;

    /**
     * 告警日期
     */
    @ApiModelProperty(value = "告警日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate alarmDate;

}
