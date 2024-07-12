 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseSceneAreaNodeConnectionModel.java.
 // File size 1153.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.generator.UuidGenerator;
import cn.cyberpeace.mp.base.model.stereotype.DefaultGen;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

/**
 * @ClassName BaseSceneAreaNodeConnectionModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
public class BaseSceneAreaNodeConnectionModel extends BaseModel {
    /**
     * 场景
     */
    private String scene;
    /**
     * 源节点id
     */
    private String sourceNodeId;
    /**
     * 目标节点id
     */
    private String targetNodeId;
    /**
     * 源节点
     */
    private String sourceNode;
    /**
     * 目标节点
     */
    private String targetNode;
    /**
     * 连接标识
     */
    @DefaultGen(UuidGenerator.class)
    private String connectionId;
    /**
     * 名称
     */
    @DefaultStr
    private String name;
    /**
     * 显示上下文
     */
    @DefaultStr
    private String displayContext;
}
