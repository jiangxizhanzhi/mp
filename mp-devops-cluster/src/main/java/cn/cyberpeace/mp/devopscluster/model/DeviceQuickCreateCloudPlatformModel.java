 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DeviceQuickCreateCloudPlatformModel.java.
 // File size 875.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.QuickCreateParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName DeviceQuickCreateCloudPlatformModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceQuickCreateCloudPlatformModel {

    /**
     * 名称
     */
    private String name;

    /**
     * 镜像名称
     */
    private String quickName;

    /**
     * 快速构建
     */
    private List<QuickCreateParam> quickCreateParams;


    public DeviceQuickCreateCloudPlatformModel(String name, String quickName) {
        this.name = name;
        this.quickName = quickName;
    }
}
