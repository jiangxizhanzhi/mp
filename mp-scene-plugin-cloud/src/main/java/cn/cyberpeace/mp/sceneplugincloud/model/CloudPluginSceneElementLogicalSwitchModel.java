 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudPluginSceneElementLogicalSwitchModel.java.
 // File size 849.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CloudPluginSceneElementLogicalSwitchModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_sceneplugincloud_scene_element_logical_switch")
public class CloudPluginSceneElementLogicalSwitchModel extends BaseModel {
    @DefaultStr
    private String scene;
    @DefaultStr
    private String name;
    @DefaultStr
    private String physicalPorts;
    @DefaultStr
    private String physicalSwitchName;
}
