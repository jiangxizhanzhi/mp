 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudPluginSceneModel.java.
 // File size 943.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CloudPluginSceneModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPluginSceneModel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_sceneplugincloud_scene")
public class CloudPluginSceneModel extends BaseCloudPluginSceneModel {

    @ApiModelProperty(value = "策略下发客户端ip")
    @DefaultStr
    private String strategyIp;

    @ApiModelProperty(value = "策略下发客户端虚拟机实例id")
    @DefaultStr
    private String strategyInstance;
}
