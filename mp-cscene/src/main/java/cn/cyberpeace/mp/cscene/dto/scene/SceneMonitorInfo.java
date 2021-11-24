package cn.cyberpeace.mp.cscene.dto.scene;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/22 11:36
 */
@Data
@NoArgsConstructor
@ApiModel(value = "场景监控信息")
public class SceneMonitorInfo {

//    @ApiModelProperty(value = "创建者")
//    private String creator;
//
//    @ApiModelProperty(value = "创建时间")
//    private LocalDateTime createTime;
//
//    @ApiModelProperty(value = "运行时间（分钟）")
//    private Integer runMinutes;
//
//    @ApiModelProperty(value = "场景属性")
//    private String property;

    @ApiModelProperty(value = "运行节点统计-虚拟机总数")
    private int virtualMachineCount = 0;

    @ApiModelProperty(value = "运行节点统计-容器总数")
    private int containerCount = 0;

    // 实体设备
    @ApiModelProperty(value = "运行节点统计-实体设备总数")
    private int entityCount = 0;


    @ApiModelProperty(value = "场景网络信息-网络总数")
    private int switchCount = 0;

    @ApiModelProperty(value = "场景网络信息-弹性IP总数")
    private int elasticIpCount = 0;

    @ApiModelProperty(value = "场景网络信息-路由总数")
    private int routerCount = 0;

    @ApiModelProperty(value = "场景网络信息-防火墙规则总数")
    private int firewallCount = 0;

    @ApiModelProperty(value = "场景网络信息-流表规则总数")
    private int flowTableCount = 0;


}
