 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseSceneNodeModel.java.
 // File size 1016.
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
 * @ClassName BaseSceneNodeModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
public class BaseSceneNodeModel extends BaseModel {
    private String scene;
    @DefaultGen(UuidGenerator.class)
    private String nodeId;
    @DefaultStr
    private String area;
    @DefaultStr
    private String icon2;
    @DefaultStr
    private String position2;
    @DefaultStr
    private String icon3;
    @DefaultStr
    private String position3;
    @DefaultStr
    private String name;
    @DefaultStr
    private String description;
    @DefaultStr
    private String displayContext;
    @DefaultStr
    private String outerPosition2;
}
