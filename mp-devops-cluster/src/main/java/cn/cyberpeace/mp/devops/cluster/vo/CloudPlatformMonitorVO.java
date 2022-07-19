package cn.cyberpeace.mp.devops.cluster.vo;

import cn.cyberpeace.mp.situationaware.model.dto.monitor.ServiceMonitorDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 云平台服务监控对象
 * @Author qiaokang
 * @Date：2022/7/16 16:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "云平台服务监控对象")
public class CloudPlatformMonitorVO {

    @ApiModelProperty("异常服务数量")
    private int errorCount;

    @ApiModelProperty("服务总数量")
    private int totalCount;

    @ApiModelProperty("告警数量")
    private int alarmCount;

    @ApiModelProperty("云平台服务监控列表")
    private List<ServiceMonitorDto> cloudPlatformMonitors = new ArrayList<>();

}
