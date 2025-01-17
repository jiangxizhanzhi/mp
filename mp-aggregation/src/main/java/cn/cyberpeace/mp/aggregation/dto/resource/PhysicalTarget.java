package cn.cyberpeace.mp.aggregation.dto.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 物理目标
 *
 * @author 二竹
 * @date 2025/01/17
 */
@Data
@Builder
@ApiModel("资源统计-实体靶标")
public class PhysicalTarget {

    @ApiModelProperty("应用系统")
    private Integer application;

    @ApiModelProperty("网络设备")
    private Integer network;

    @ApiModelProperty("操作系统")
    private Integer operateSystem;

    @ApiModelProperty("安全设备")
    private Integer security;

}
