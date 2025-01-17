 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseObjMappingModel.java.
 // File size 660.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.model;

import cn.cyberpeace.mp.base.model.generator.UuidGenerator;
import cn.cyberpeace.mp.base.model.stereotype.DefaultGen;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

/**
 * 对象映射模型基类
 */
@Data
public abstract class BaseObjMappingModel extends BaseModel {
    @DefaultStr
    private String group;
    @DefaultInt
    private Integer seq;
    @DefaultGen(UuidGenerator.class)
    private String name;

    public abstract String retrieveSource();

    public abstract String retrieveTarget();
}
