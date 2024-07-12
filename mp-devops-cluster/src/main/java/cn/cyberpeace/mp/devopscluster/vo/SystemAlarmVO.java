 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SystemAlarmVO.java.
 // File size 1411.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName SystemAlarmVO
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "系统告警对象")
public class SystemAlarmVO {

    public SystemAlarmVO(CloudPlatformMonitorVO cloudPlatformMonitorVO, BizServiceMonitorVO bizServiceMonitorVO,
                         ServerMonitorVO serverMonitorVO) {
        this.cloudPlatformMonitorVO = cloudPlatformMonitorVO;
        this.bizServiceMonitorVO = bizServiceMonitorVO;
        this.serverMonitorVO = serverMonitorVO;
    }

    @ApiModelProperty("云平台服务监控")
    private CloudPlatformMonitorVO cloudPlatformMonitorVO;

    @ApiModelProperty("业务服务监控")
    private BizServiceMonitorVO bizServiceMonitorVO;

    @ApiModelProperty("服务器监控")
    private ServerMonitorVO serverMonitorVO;

    @ApiModelProperty("服务告警趋势")
    List<AlarmStatisticsVO> serviceAlarmList = new ArrayList<>();

    @ApiModelProperty("设备告警趋势")
    List<AlarmStatisticsVO> serverAlarmList = new ArrayList<>();

}
