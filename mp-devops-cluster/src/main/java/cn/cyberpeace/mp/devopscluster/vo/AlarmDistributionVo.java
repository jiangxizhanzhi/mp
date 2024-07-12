 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AlarmDistributionVo.java.
 // File size 744.
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
 * @ClassName AlarmDistributionVo
 * @Description
 * @Author shgang
 * @Date 2022/11/12
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "系统告警分布")
public class AlarmDistributionVo {

    @ApiModelProperty("云平台服务告警数量")
    private Long cpAlarmCount;

    @ApiModelProperty("业务服务告警数量")
    private Long crAlarmCount;

    @ApiModelProperty("服务器告警数量")
    private Long serverAlarmCount;

}
