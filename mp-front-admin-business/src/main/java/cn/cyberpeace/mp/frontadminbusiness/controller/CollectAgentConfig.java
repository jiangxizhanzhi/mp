package cn.cyberpeace.mp.frontadminbusiness.controller;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollectAgentConfig {
    @ApiModelProperty(value = "采集开关")
    private Boolean enable;
    @ApiModelProperty(value = "采集周期，单位秒")
    private Integer period;
    @ApiModelProperty(value = "采集内容-信息")
    private boolean info;
    @ApiModelProperty(value = "采集内容-事件")
    private boolean event;
    @ApiModelProperty(value = "采集内容-日志")
    private boolean log;
    @ApiModelProperty(value = "采集内容-流量")
    private boolean flow;

    public static CollectAgentConfig defaultValue(){
        CollectAgentConfig agentConfig = new CollectAgentConfig();
        agentConfig.setEnable(true);
        agentConfig.setPeriod(180);
        agentConfig.setInfo(true);
        agentConfig.setFlow(true);
        agentConfig.setEvent(true);
        agentConfig.setLog(true);
        return agentConfig;
    }
}
