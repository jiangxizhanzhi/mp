 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectionPluginSceneDataModel.java.
 // File size 950.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CollectionPluginSceneDataModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePluginSceneElementModel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_sceneplugincollection_scene_data")
public class CollectionPluginSceneDataModel extends BasePluginSceneElementModel {
    @DefaultStr
    private String data;
    @DefaultInt
    private Integer type;

    public interface Type {
        int PORT = 1;
        int NETIFACE = 2;
        int LOGFile = 3;
    }
}
