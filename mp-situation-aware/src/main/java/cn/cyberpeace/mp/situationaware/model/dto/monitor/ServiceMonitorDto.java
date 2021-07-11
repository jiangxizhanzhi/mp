package cn.cyberpeace.mp.situationaware.model.dto.monitor;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMonitorDto {

    @ApiModelProperty(value = "运行ID")
    private String launchId;

    @ApiModelProperty(value = "主机名")
    private String hostname;

    

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "服务名称")
    private String serviceName;

    
    @ApiModelProperty(value = "运行时长（秒）")
    private Long uptime;

    
    @ApiModelProperty(value = "状态 ERROR, DOWN, RUNNING, RESTARTING")
    private String state;

    @ApiModelProperty(value = "IP地址")
    private String ip;

    
    
    @ApiModelProperty(value = "是否禁止重启")
    private Boolean disableRestart = false;

    
    @ApiModelProperty(value = "采集时间")
    private LocalDateTime scanTime;

}
