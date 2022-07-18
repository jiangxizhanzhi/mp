package cn.cyberpeace.mp.devops.cluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 系统告警对象
 * @Author qiaokang
 * @Date：2022/7/16 17:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "系统告警对象")
public class SystemAlarmVO {

    @ApiModelProperty("云平台服务监控")
    private CloudPlatformMonitorVO cloudPlatformMonitorVO;

    @ApiModelProperty("业务服务监控")
    private BizServiceMonitorVO bizServiceMonitorVO;

    @ApiModelProperty("服务器监控")
    private ServerMonitorVO serverMonitorVO;

}
