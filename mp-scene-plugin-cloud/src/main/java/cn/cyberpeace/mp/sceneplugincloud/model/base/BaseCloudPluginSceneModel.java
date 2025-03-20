 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseCloudPluginSceneModel.java.
 // File size 668.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneModel;
import lombok.Data;

/**
 * @ClassName BaseCloudPluginSceneModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePluginSceneModel
 */
@Data
public class BaseCloudPluginSceneModel extends BasePluginSceneModel {
    @DefaultBool
    private Boolean enableRecording;
    @DefaultBool
    private Boolean enableRate;
    @DefaultStr
    private String dnsNameservers;
    @DefaultStr
    private String flowMirrorIp;
    @DefaultStr
    private String flowMirrorId;
    @DefaultStr
    private String flowMirrors ;
}
