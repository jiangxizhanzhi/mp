 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseObjMappingModel.java.
 // File size 786.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.model;

import cn.cyberpeace.mp.base.model.generator.UuidGenerator;
import cn.cyberpeace.mp.base.model.stereotype.DefaultGen;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

/**
 * @ClassName BaseObjMappingModel
 * @Description 对象映射模型基类
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
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
