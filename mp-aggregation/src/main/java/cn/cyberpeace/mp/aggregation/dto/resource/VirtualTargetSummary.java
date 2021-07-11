package cn.cyberpeace.mp.aggregation.dto.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@ApiModel("资源统计-虚拟靶标")
public class VirtualTargetSummary {

    @ApiModelProperty("应用系统")
    private Integer application;

    @ApiModelProperty("网络设备")
    private Integer network;

    @ApiModelProperty("操作系统")
    private Integer operateSystem;

    @ApiModelProperty("安全设备")
    private Integer security;

}
