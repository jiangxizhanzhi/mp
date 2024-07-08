package cn.cyberpeace.mp.scenepluginflow.plugin.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用于承载流量ID和源IP信息的类。
 * <p>
 * 该类通过封装流量ID和源IP，提供了一种简单的方式來标识和追踪流量。
 */
@Data
public class FlowIdWithSourceIp {
    /**
     * 流量ID，用于唯一标识一个流量。
     */
    @ApiModelProperty(value = "流量id")
    private String flowId;

    /**
     * 源IP，标识流量的来源。
     */
    @ApiModelProperty(value = "源ip")
    private String sourceIp;
}

