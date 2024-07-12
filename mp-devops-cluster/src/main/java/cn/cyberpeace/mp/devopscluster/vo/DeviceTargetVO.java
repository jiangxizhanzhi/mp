 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DeviceTargetVO.java.
 // File size 1207.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName DeviceTargetVO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceTargetVO {

    /**
     * 镜像UUID
     */
    @ApiModelProperty("UUID")
    private String uuid;

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
     * 角色
     */
    @ApiModelProperty("角色")
    private Integer role;

    /**
     * 分类
     */
    @ApiModelProperty("一级分类")
    private Integer category;

    /**
     * 子分类
     */
    @ApiModelProperty("二级分类")
    private Integer secondCategory;

    /**
     * 子分类
     */
    @ApiModelProperty("三级分类")
    private Integer thirdCategory;

    /**
     * 镜像类型
     */
    @ApiModelProperty("镜像类型")
    private String imageType;
}
