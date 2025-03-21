// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name SceneAreaModel.java.
// File size 479.
// File create date Fri Jul 12 12:55:30 CST 2024.
// File update date Fri Jul 12 12:55:30 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.scene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_scene_snapshot")
public class SceneSnapshotModel extends BaseModel {
    @DefaultStr
    private String csceneId;
    @DefaultStr
    private String csceneInstanceId;
    @DefaultStr
    private String snapshotName;
    @DefaultStr
    private String content;
}
