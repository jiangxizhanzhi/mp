 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RealEquipmentPageRequest.java.
 // File size 1383.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @ClassName RealEquipmentPageRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePageRequest
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RealEquipmentPageRequest extends BasePageRequest{

    /**
     * 系统主类型
     */
    @ApiModelProperty(value = "一级类型")
    private Integer category;

    /**
     * 系统子类型
     */
    @ApiModelProperty(value = "二级类型")
    private Integer secondCategory;

    /**
     * 系统子类型
     */
    @ApiModelProperty(value = "三级类型")
    private Integer thirdCategory;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 在位状态
     */
    @ApiModelProperty(value = "在位状态")
    private Integer inPosition;

    /**
     * 使用状态
     */
    @ApiModelProperty(value = "使用状态")
    private Integer useStatus;

    /**
     * 精确查找名称
     */
    @ApiModelProperty(value = "精确查找名称")
    private String exactName;
}
