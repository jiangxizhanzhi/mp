 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DeviceAndSnapshotConvertModel.java.
 // File size 690.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName DeviceAndSnapshotConvertModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class DeviceAndSnapshotConvertModel{

    /**
     * 设备ID
     */
    @ApiModelProperty("镜像UUID")
    private String deviceUuid;

    /**
     * 设备名称
     */
    @ApiModelProperty("镜像名称")
    private String deviceName;

    /**
     * 快照列表
     */
    @ApiModelProperty("快照列表")
    private List<SnapshotConvertModel> items;

}
