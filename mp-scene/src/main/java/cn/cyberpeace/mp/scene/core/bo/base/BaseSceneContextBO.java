 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseSceneContextBO.java.
 // File size 312.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.bo.base;

import lombok.Data;

import java.util.Map;

/**
 * @ClassName BaseSceneContextBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public abstract class BaseSceneContextBO {
    private Map<String, Object> plugins;
}
