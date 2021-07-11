package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


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
