 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ExtDocker.java.
 // File size 2165.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ExtDocker
 * @Description 扩展容器信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtDocker implements Serializable {

    @ApiModelProperty("flavor信息")
    private String flavor;

    @ApiModelProperty("CPU核数")
    private Integer cpu;

    @ApiModelProperty("内存大小")
    private Integer ram;

    @ApiModelProperty("磁盘大小")
    private Integer disk;

    /**
     * 文件地址
     */
    @ApiModelProperty(value = "文件地址")
    private String file;

    /**
     * 启动参数
     */
    @ApiModelProperty(value = "启动参数",hidden = true)
    private String settings;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private Integer syncStatus;

    /**
     * 临时文件ID
     */
    @ApiModelProperty("临时文件ID")
    private String tmpdirUuid;


    @ApiModelProperty("用户文件名称")
    private String userFilename;


    @ApiModelProperty("实例ID")
    private String instanceUuid;

    /**
     * 镜像状态 0:没有实例，1：创建中，2创建成功，3创建失败
     */
    @ApiModelProperty("实例状态")
    private Integer instanceState;

    /**
     * 支持初始化
     */
    @ApiModelProperty("支持初始化")
    private Boolean initSupport;

    /**
     * 支持快速创建
     */
    @ApiModelProperty("支持快速创建")
    private Boolean quickCreateSupport;

    @ApiModelProperty("快照数量")
    private Integer snapshotCount;

    /**
     * 额外信息
     */
    @ApiModelProperty("扩展信息")
    private Object extInfo;


    /**
     * 接入方式
     */
    @ApiModelProperty("接入方式")
    private List<AccessProtocol> accessProtocols;
}
