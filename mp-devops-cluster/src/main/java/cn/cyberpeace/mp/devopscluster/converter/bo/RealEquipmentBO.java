 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RealEquipmentBO.java.
 // File size 2876.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter.bo;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devopscluster.converter.model.BasePageConvertModel;
import cn.cyberpeace.mp.devopscluster.enums.PublicEnum;
import cn.cyberpeace.mp.devopscluster.enums.StatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * @ClassName RealEquipmentBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePageConvertModel
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RealEquipmentBO extends BasePageConvertModel {

    /**
     * UUID
     */
    private String uuid;

    /**
     * 名称
     */
    private String name;

    /**
     * 图标
     */
    private String logo;

    /**
     * 角色
     */
    private Integer role;

    /**
     * 分类
     */
    private Integer mainCategory;

    /**
     * 子分类
     */
    private Integer subCategory;

    /**
     * 三级分类
     */
    private Integer thirdCategory;

    /**
     * 镜像类型
     */
    private String imageType;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否导入
     */
    private Boolean isImport;

    /**
     * 是否默认靶标
     */
    private Boolean isInternal;

    /**
     * 系统信息
     */
    private ExtSystemInfo extSystemInfo;

    /**
     * 实体信息
     */
    private ExtEntity extEntity;


    /**
     * 镜像信息
     */
    private ExtImageInfo extImageInfo;

    /**
     * 交换机
     */
    private List<ExtEntityAccess> extEntityAccess;

    /**
     * 虚拟机信息
     */
    private ExtVm extVm;

    /**
     * 网关信息
     */
    private ExtGateway extGateway;

    /**
     * Docker容器信息
     */
    private ExtDocker extDocker;

    /**
     * 是否公开
     */
    private PublicEnum isPublic;

    /**
     * 授权人数
     */
    private Integer authTotal;

    /**
     * 在位状态
     */
    private StatusEnum inPosition;

    /**
     * 使用状态
     */
    private StatusEnum useStatus;

    /**
     * 引用状态
     */
    private Integer referenceStatus;

    /**
     * 实体USB信息
     */
    private EntityUsb extEntityUsb;

    /**
     * 是否开启syslog采集
     */
    private Boolean openSyslog;

    /**
     * 是否开启kafka采集
     */
    private Boolean openKafka;

    /**
     * 是否开启restapi采集
     */
    private Boolean openRestapi;

    /**
     * 是否开启webservice(soap)采集
     */
    private Boolean openSoap;

    /**
     * 是否开启snmp采集
     */
    private Boolean openSnmp;

    private String entityJumpUrl;
    private String entityUserName;
    private String entityPassword;
}
