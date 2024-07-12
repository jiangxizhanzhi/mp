 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AlarmStatisticsTrendVO.java.
 // File size 730.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName AlarmStatisticsTrendVO
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
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
