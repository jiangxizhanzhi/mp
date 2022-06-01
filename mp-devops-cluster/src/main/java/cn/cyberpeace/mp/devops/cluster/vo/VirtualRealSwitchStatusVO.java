package cn.cyberpeace.mp.devops.cluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 虚实结合交换机状态
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "虚实结合交换机状态")
public class VirtualRealSwitchStatusVO extends BaseVO {

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 管理IP
     */
    @ApiModelProperty("管理IP")
    private String managementIp;

    /**
     * 节点管理
     */
    @ApiModelProperty("节点管理")
    private String state;

    /**
     * 端口总数
     */
    @ApiModelProperty("端口总数")
    private Integer ports;

    /**
     * 已使用端口数
     */
    @ApiModelProperty("已使用端口数")
    private Integer usedPorts;
}
