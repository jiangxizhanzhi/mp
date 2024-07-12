 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RealEquipmentNetworkCardPageVO.java.
 // File size 904.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName RealEquipmentNetworkCardPageVO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseVO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "系统运维-实装管理接口资源返回信息")
public class RealEquipmentNetworkCardPageVO extends BaseVO {

    /**
     * 名称
     */
    @ApiModelProperty("接口名称")
    private String name;

    /**
     * 入流量
     */
    @ApiModelProperty("入流量")
    private Long inflow;

    /**
     * 出流量
     */
    @ApiModelProperty("出流量")
    private Long outflow;

}
