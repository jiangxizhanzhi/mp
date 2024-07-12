 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneStat.java.
 // File size 1354.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName CsceneStat
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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
