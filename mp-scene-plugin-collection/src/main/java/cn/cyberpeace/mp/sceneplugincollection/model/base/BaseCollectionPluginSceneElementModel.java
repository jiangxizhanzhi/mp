 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseCollectionPluginSceneElementModel.java.
 // File size 662.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementModel;
import lombok.Data;

/**
 * @ClassName BaseCollectionPluginSceneElementModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePluginSceneElementModel
 */
@Data
public class BaseCollectionPluginSceneElementModel extends BasePluginSceneElementModel {
    @DefaultBool
    private Boolean enableProbeCollect;
    @DefaultStr
    private String probes;
}
