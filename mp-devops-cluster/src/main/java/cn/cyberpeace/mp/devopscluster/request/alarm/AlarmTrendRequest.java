package cn.cyberpeace.mp.devopscluster.request.alarm;

import cn.cyberpeace.mp.devopscluster.request.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AlarmTrendRequest extends BaseRequest implements Serializable {

    
    @ApiModelProperty(value = "告警类型：0:云平台服务, 1:业务服务 2:设备，可多选，如 [0,1] 表示统计云平台和业务服务趋势")
    private Integer[] types;

    
    @ApiModelProperty(value = "单位: 年(year)，月份(month)，天数(date), 小时(hour)")
    private String unit;

    
    @ApiModelProperty(value = "数量: 年(10)，月份(12)，天数(30)，小时(24)")
    private Integer count;

    
    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    
    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

}
