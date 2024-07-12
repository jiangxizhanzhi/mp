 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualUsbStorage.java.
 // File size 820.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName VirtualUsbStorage
 * @Description 虚拟USB存储信息
 * @Author guquan
 * @Date 2023/4/18 10:30
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
@ApiModel("虚拟USB存储信息")
public class VirtualUsbStorage extends BaseModel {

    /**
     * 容量
     */
    @ApiModelProperty("容量")
    private Integer capacity;

    /**
     * 已使用
     */
    @ApiModelProperty("已使用")
    private Integer used;

    /**
     * 文件列表
     */
    @ApiModelProperty("文件列表")
    private VirtualUsbFileList files;
}
