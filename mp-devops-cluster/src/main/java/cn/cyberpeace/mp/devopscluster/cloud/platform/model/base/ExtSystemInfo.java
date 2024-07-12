 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ExtSystemInfo.java.
 // File size 1445.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName ExtSystemInfo
 * @Description 扩展系统信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("系统信息")
public class ExtSystemInfo implements Serializable {

    /**
     * 操作系统
     */
    @ApiModelProperty("二级系统类型")
    private Integer parentSystemType;

    /**
     * 操作系统
     */
    @ApiModelProperty("三级系统类型")
    private Integer systemType;

    /**
     * 系统架构
     */
    @ApiModelProperty("系统架构")
    private String systemArchitecture;

    /**
     * 语言
     */
    @ApiModelProperty("语言")
    private String language;

    /**
     * 系统类型名称
     */
    @ApiModelProperty("系统类型名称")
    private String systemTypeName;

    /**
     * 系统架构名称
     */
    @ApiModelProperty("系统架构名称")
    private String systemArchitectureName;

    /**
     * 语言名称
     */
    @ApiModelProperty("语言名称")
    private String languageName;
}
