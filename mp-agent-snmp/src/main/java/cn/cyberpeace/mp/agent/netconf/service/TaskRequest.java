package cn.cyberpeace.mp.agent.netconf.service;

import cn.cyberpeace.mp.agent.snmp.service.CommandConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TaskRequest<T extends BasicAccessInfo> {
    @ApiModelProperty("下发任务ID")
    private String taskId;

    @ApiModelProperty("目标节点接入")
    private T accessInfo;

    @ApiModelProperty("配置信息")
    private CommandConfig config;

    @ApiModelProperty("插件回调地址")
    private String reportUrl;

}
