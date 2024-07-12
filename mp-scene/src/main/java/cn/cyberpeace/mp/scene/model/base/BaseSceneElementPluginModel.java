 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseSceneElementPluginModel.java.
 // File size 428.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import lombok.Data;

/**
 * @ClassName BaseSceneElementPluginModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
public class BaseSceneElementPluginModel extends BaseModel {
    private String scene;
    private String sceneElement;
    private String pluginKey;
}
