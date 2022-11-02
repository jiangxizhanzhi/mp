package cn.cyberpeace.mp.devopscluster.request.alarm;

import cn.cyberpeace.mp.devopscluster.request.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description 告警趋势请求
 * @Author zhouxinghuai
 * @Date 2022-11-01 11:35:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AlarmTrendRequest extends BaseRequest implements Serializable {

    /**
     * 告警类型
     */
    @ApiModelProperty(value = "告警类型：0:云平台服务, 1:业务服务 2:设备，可多选，如 [0,1] 表示统计云平台和业务服务趋势")
    private Integer[] types;

    /**
     * 统计单位
     */
    @ApiModelProperty(value = "单位: 年(year)，月份(month)，星期(week)， 天数(date), 默认为月份(month)")
    private String unit;

    /**
     * 默认显示数量
     */
    @ApiModelProperty(value = "数量: 年(10)，月份(12)，星期(10)，天数(30)， 默认为月份(12)")
    private Integer count;

}
