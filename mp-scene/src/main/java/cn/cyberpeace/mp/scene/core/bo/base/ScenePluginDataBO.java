 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ScenePluginDataBO.java.
 // File size 464.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.bo.base;

import lombok.Data;

import java.util.Map;

/**
 * @ClassName ScenePluginDataBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseScenePluginDataBO
 */
@Data
public class ScenePluginDataBO extends BaseScenePluginDataBO<Map<String, Object>, Map<String, Object>,
        Map<String, Object>> {

    public ScenePluginDataBO export() {
        return this;
    }
}
