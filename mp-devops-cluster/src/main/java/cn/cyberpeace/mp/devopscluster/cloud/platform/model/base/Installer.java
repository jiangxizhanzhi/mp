 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Installer.java.
 // File size 1380.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName Installer
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPlatformResult
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Installer extends BaseCloudPlatformResult {

    /**
     * ID
     */
    private Integer id;

    /**
     * UUID
     */
    private String uuid;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 图标
     */
    private String logo;

    /**
     * 分类
     */
    private String categories;

    /**
     * 服务类型：windows服务/docker服务
     */
    private Integer deployType;

    /**
     * 对应的docker服务的扩展信息
     */
    private ExtDocker extDocker;

    /**
     * 对应的windows服务扩展信息
     */
    private ExtNormal extNormal;

    /**
     * windows服务存放资源
     */
    private List<Resource> resourceList;

    /**
     * 关联镜像数量
     */
    private Integer deviceCount;
}
