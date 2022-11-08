package cn.cyberpeace.mp.situationaware.model.dto.monitor;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/15 09:05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMonitorDto {

    @ApiModelProperty(value = "运行ID")
    private String launchId;

    @ApiModelProperty(value = "主机名")
    private String hostname;

    //private String type;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "服务名称")
    private String serviceName;

    // 运行时长（秒）
    @ApiModelProperty(value = "运行时长（秒）")
    private Long uptime;

    // ERROR, DOWN, RUNNING, RESTARTING
    @ApiModelProperty(value = "状态 ERROR, DOWN, RUNNING, RESTARTING")
    private String state;

    @ApiModelProperty(value = "IP地址")
    private String ip;

    // 如果某个服务在restart-restrict-list表中，且状态是RUNNING || RESTARTING，则禁止该服务重启，既disableRestart=true，
    // 为false（默认值）表示允许重启，1：服务不在限制列表中；2：服务在限制列表中，但是状态不是RUNNING
    @ApiModelProperty(value = "是否禁止重启")
    private Boolean disableRestart = false;

    // 采集时间
    @ApiModelProperty(value = "采集时间")
    private LocalDateTime scanTime;

}
