 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ImageConvertModel.java.
 // File size 3083.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter.model;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devopscluster.enums.PublicEnum;
import cn.cyberpeace.mp.devopscluster.enums.StatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName ImageConvertModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePageConvertModel
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ImageConvertModel extends BasePageConvertModel{

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
     * 是否初始靶标
     */
    private Boolean isInit;

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
     * 是否支持导出
     */
    private Boolean supportExport;

    /**
     * 靶标创建用户名称
     */
    private String createUserName;

    /**
     * 模型ID
     */
    private String modelId;

    /**
     * 模型CODE
     */
    private String modelCode;

    @ApiModelProperty("是否是攻防靶标模拟器")
    private Boolean attackMockDeviceFlag;

    @ApiModelProperty("是否支持行为仿真")
    private Boolean behaveSupport;

    /**
     * 攻防模拟器类型 0-攻击 1-防守
     */
    private Integer attackMockDeviceType;

    @ApiModelProperty("是否支持qga")
    private Boolean supportQga;

    @ApiModelProperty("是否是操作机")
    private Boolean operateMachine;

    @ApiModelProperty("是否可以参训人员使用")
    private Boolean canParticipantsUse;

    @ApiModelProperty("是否支持数据采集")
    private Boolean supportCollect;

    @ApiModelProperty("扩展字段")
    private Object ext;

}
