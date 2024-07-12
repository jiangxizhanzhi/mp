 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectionPluginSceneModel.java.
 // File size 1073.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.sceneplugincollection.model.base.BaseCollectionPluginSceneModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CollectionPluginSceneModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCollectionPluginSceneModel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_sceneplugincollection_scene")
public class CollectionPluginSceneModel extends BaseCollectionPluginSceneModel {
    @DefaultStr
    private String flowCollector;

    @DefaultStr
    private String flowCollectorContainerId;

    @DefaultStr
    private String probeCollector;

    @DefaultStr
    private String probeRouter;

    @DefaultStr
    private String flowFile;

    @DefaultStr
    private String flowMirrorId;

    @DefaultStr
    private String optionKey;

}
