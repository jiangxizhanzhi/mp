 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name InstallerConvertModel.java.
 // File size 1708.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter.model;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtDocker;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtNormal;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Resource;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.StartUpParameter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName InstallerConvertModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePageConvertModel
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class InstallerConvertModel extends BasePageConvertModel{

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
     * 启动参数（包装对象）
     */
    private List<StartUpParameter> startUpParameterList;

    /**
     * 对应的windows服务扩展信息
     */
    private ExtNormal extNormal;


    /**
     * 资源列表
     */
    private List<Resource> resourceList = new ArrayList<>();


    /**
     * 关联镜像数量
     */
    private Integer deviceCount;

}
