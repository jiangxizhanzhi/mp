package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "告警数量统计信息")
public class AlarmStatisticsTrendVO {

    
    @ApiModelProperty(value = "告警数量")
    private int alarmCount;

    
    @ApiModelProperty(value = "告警时间")
    private String timeBucket;

}
