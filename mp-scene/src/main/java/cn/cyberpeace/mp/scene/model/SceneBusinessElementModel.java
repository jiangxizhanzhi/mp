// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name SceneElementModel.java.
// File size 1822.
// File create date Fri Jul 12 12:55:30 CST 2024.
// File update date Fri Jul 12 12:55:30 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.scene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.generator.UuidGenerator;
import cn.cyberpeace.mp.base.model.stereotype.DefaultGen;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_scene_business_element")
public class SceneBusinessElementModel extends BaseModel {
    @DefaultStr
    private String scene;
    @DefaultGen(UuidGenerator.class)
    private String nodeId;
    @DefaultStr
    private String name;
    @DefaultInt
    private Integer cpu;
    @DefaultInt
    private Integer ram;
    @DefaultInt
    private Integer disk;
    @DefaultStr
    private String resourceId;
    @DefaultStr
    private String imageType;
    @DefaultStr
    private String image;
    @DefaultStr
    private String image_uuid;
}
