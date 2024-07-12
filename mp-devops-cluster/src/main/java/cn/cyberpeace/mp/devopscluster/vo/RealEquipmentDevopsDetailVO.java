 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RealEquipmentDevopsDetailVO.java.
 // File size 2753.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.EntityUsb;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtEntity;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtSystemInfo;
import cn.cyberpeace.mp.devopscluster.model.common.TargetConnection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName RealEquipmentDevopsDetailVO
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
@ApiModel(description = "系统运维-实装管理详情返回信息")
public class RealEquipmentDevopsDetailVO extends BaseVO {

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 图标
     */
    @ApiModelProperty("图标")
    private String logo;

    /**
     * 管理地址
     */
    @ApiModelProperty("管理地址")
    private List<String> address;

    /**
     * 型号
     */
    @ApiModelProperty("型号")
    private String model;

    /**
     * 参数
     */
    @ApiModelProperty(value = "参数")
    private String params;

    /**
     * 系统信息
     */
    @ApiModelProperty("系统信息")
    private ExtSystemInfo extSystemInfo;

    /**
     * 实体信息
     */
    @ApiModelProperty("实体信息")
    private ExtEntity extEntity;

    /**
     * 实体USB
     */
    @ApiModelProperty("实体USB")
    private EntityUsb extEntityUsb;

    /**
     * CPU使用率
     */
    @ApiModelProperty("CPU使用率")
    private BigDecimal cpuPercent;

    /**
     * 内存使用率
     */
    @ApiModelProperty("内存使用率")
    private BigDecimal ramPercent;

    /**
     * 磁盘使用率
     */
    @ApiModelProperty("磁盘使用率")
    private BigDecimal diskPercent;


    /**
     * 登录链接信息
     */
    @ApiModelProperty("登录链接信息")
    private List<TargetConnection> connections;

    @ApiModelProperty("是否开启syslog采集")
    private Boolean openSyslog;

    @ApiModelProperty("是否开启kafka采集")
    private Boolean openKafka;

    @ApiModelProperty("是否开启restapi采集")
    private Boolean openRestapi;

    @ApiModelProperty("是否开启webservice(soap)采集")
    private Boolean openSoap;

    @ApiModelProperty("是否开启snmp采集,0不开启,1开启")
    private Boolean openSnmp;
}
