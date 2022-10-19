package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TeamFlowAggVO {
    @ApiModelProperty(value = "流量")
    private FlowPercent flow;

    @ApiModelProperty(value = "攻击次数")
    private FlowPercent attack;

    @ApiModelProperty(value = "扰动次数")
    private FlowPercent disturbance;

    @Data
    public static class FlowPercent{
        private long size;

        private long count;

        private Double percent;
    }
}
