package cn.cyberpeace.mp.devopscluster.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "告警数量统计信息")
public class AlarmStatisticsVO {

    
    @ApiModelProperty(value = "告警id")
    private String id;

    
    @ApiModelProperty(value = "告警数量")
    private int alarmCount;

    
    @ApiModelProperty(value = "告警日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate alarmDate;

}
