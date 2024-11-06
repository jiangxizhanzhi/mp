 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RealEquipmentPageVO.java.
 // File size 3178.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.EntityUsb;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtSystemInfo;
import cn.cyberpeace.mp.devopscluster.enums.StatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName RealEquipmentPageVO
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
@ApiModel(description = "实装分页返回信息")
public class RealEquipmentPageVO extends BaseVO {

    /**
     * ID
     */
    @ApiModelProperty(value = "ID")
    private String id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 型号
     */
    @ApiModelProperty(value = "型号")
    private String model;

    /**
     * 参数
     */
    @ApiModelProperty(value = "参数")
    private String params;

    /**
     * 分类
     */
    @ApiModelProperty(value = "一级分类")
    private Integer category;

    /**
     * 分类
     */
    @ApiModelProperty(value = "一级分类名称")
    private String categoryName;


    /**
     * 子分类
     */
    @ApiModelProperty(value = "二级分类")
    private Integer subCategory;

    /**
     * 子分类
     */
    @ApiModelProperty(value = "二级分类名称")
    private String subCategoryName;


    /**
     * 子分类
     */
    @ApiModelProperty(value = "三级分类")
    private Integer thirdCategory;

    /**
     * 三级分类名称
     */
    @ApiModelProperty(value = "三级分类名称")
    private String thirdCategoryName;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 实体类型
     */
    @ApiModelProperty(value = "实体类型")
    private String entityType;

    /**
     * 实体类型名称
     */
    @ApiModelProperty(value = "实体类型名称")
    private String entityTypeName;

    /**
     * 实体详情
     */
    @ApiModelProperty(value = "实体详情")
    private String entityDetail;

    /**
     * 在位状态
     */
    @ApiModelProperty(value = "在位状态,0:不在位，1：在位")
    private StatusEnum inPosition;

    /**
     * 引用状态
     */
    @ApiModelProperty(value = "引用状态")
    private Integer referenceStatus;

    /**
     * 使用状态
     */
    @ApiModelProperty(value = "使用状态，0：空闲，1：使用")
    private StatusEnum useStatus;

    /**
     * 系统信息
     */
    @ApiModelProperty("系统信息")
    private ExtSystemInfo extSystemInfo;

    /**
     * 实体USB
     */
    @ApiModelProperty("实体USB")
    private EntityUsb extEntityUsb;

    /**
     * 是否可登录
     */
    @ApiModelProperty("是否可登录")
    private Boolean loginEnable;

    @ApiModelProperty(value = "实体设备跳转地址")
    private String entityJumpUrl;
    @ApiModelProperty(value = "实体设备用户名")
    private String entityUserName;
    @ApiModelProperty(value = "实体设备密码")
    private String entityPassword;
}
