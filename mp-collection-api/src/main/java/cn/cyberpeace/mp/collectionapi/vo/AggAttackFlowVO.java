package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AggAttackFlowVO {
    @ApiModelProperty(value = "聚合标识,跟随请求")
    private String name;

    @ApiModelProperty(value = "攻击次数")
    private long attackCount;

    @ApiModelProperty(value = "流量大小")
    private long size;
}
