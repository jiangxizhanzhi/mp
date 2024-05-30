package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@ApiModel(description = "场景模板统计数据")
@Data
@AllArgsConstructor
public class CsceneStat {
    @ApiModelProperty(value = "场景模板id")
    private String csceneId;
    @ApiModelProperty(value = "终端数量")
    private Integer terminalCount;
    @ApiModelProperty(value = "路由数量")
    private Integer routerCount;
    @ApiModelProperty(value = "防火墙数量")
    private Integer firewallCount;
    @ApiModelProperty(value = "交换机数量")
    private Integer switchCount;
    @ApiModelProperty(value = "npc数量")
    private Integer npcCount;
    @ApiModelProperty(value = "测试场景数量")
    private Integer testSceneCount;
    @ApiModelProperty(value = "占用节点资源数")
    private Integer resourceOccupiedNums;
    @ApiModelProperty(value = "总cpu数量,单位个")
    private Integer totalCpu;
    @ApiModelProperty(value = "总磁盘大小,单位G")
    private Integer totalDisk;
    @ApiModelProperty(value = "总内存大小,单位M")
    private Integer totalRam;
}
