package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 告警数量统计信息
 * @Author qiaokang
 * @Date：2022/7/19 19:49
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "告警数量统计信息")
public class AlarmStatisticsTrendVO {

    /**
     * 告警数量
     */
    @ApiModelProperty(value = "告警数量")
    private int alarmCount;

    /**
     * 告警时间
     */
    @ApiModelProperty(value = "告警时间")
    private String timeBucket;

}
