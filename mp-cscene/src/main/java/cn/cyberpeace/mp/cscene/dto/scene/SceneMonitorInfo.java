package cn.cyberpeace.mp.cscene.dto.scene;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "场景监控信息")
public class SceneMonitorInfo {

    @ApiModelProperty(value = "场景实例类型")
    private Integer type;

    @ApiModelProperty(value = "场景实例引用类型")
    private Integer referenceType;

    @ApiModelProperty(value = "场景实例引用元数据")
    private String referenceMetaData;

    @ApiModelProperty(value = "创建者")
    private String createUsername;
    
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    
    @ApiModelProperty(value = "运行时间（分钟）")
    private Long runMinutes;
    
    @ApiModelProperty(value = "场景属性 0:无隔离 1:个人隔离 2:团队隔离")
    private Integer sceneAttr;

    @ApiModelProperty(value = "运行节点统计-虚拟机总数")
    @Builder.Default
    private int virtualMachineCount = 0;

    @ApiModelProperty(value = "运行节点统计-容器总数")
    @Builder.Default
    private int containerCount = 0;

    
    @ApiModelProperty(value = "运行节点统计-实体设备总数")
    @Builder.Default
    private int entityCount = 0;


    @ApiModelProperty(value = "场景网络信息-网络总数")
    @Builder.Default
    private int switchCount = 0;

    @ApiModelProperty(value = "场景网络信息-弹性IP总数")
    @Builder.Default
    private int elasticIpCount = 0;

    @ApiModelProperty(value = "场景网络信息-路由总数")
    @Builder.Default
    private int routerCount = 0;

    @ApiModelProperty(value = "场景网络信息-防火墙规则总数")
    @Builder.Default
    private int firewallCount = 0;

    @ApiModelProperty(value = "场景网络信息-流表规则总数")
    @Builder.Default
    private int flowTableCount = 0;


}
