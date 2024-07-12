 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AlarmTrendRequest.java.
 // File size 1700.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
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

/**
 * @ClassName AlarmTrendRequest
 * @Description 告警趋势请求
 * @Author zhouxinghuai
 * @Date 2022-11-01 11:35:04
 * @Version 1.0.0
 * @see BaseRequest
 * @see Serializable
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
    @ApiModelProperty(value = "单位: 年(year)，月份(month)，天数(date), 小时(hour)")
    private String unit;

    /**
     * 默认显示数量
     */
    @ApiModelProperty(value = "数量: 年(10)，月份(12)，天数(30)，小时(24)")
    private Integer count;

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

}
