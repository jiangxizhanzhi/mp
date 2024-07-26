// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name CollectionPluginSceneConfigElementModel.java.
// File size 805.
// File create date Fri Jul 12 12:55:24 CST 2024.
// File update date Fri Jul 12 12:55:24 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.sceneplugincollection.model.base.BaseCollectionPluginSceneElementModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CollectionPluginSceneConfigElementModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCollectionPluginSceneElementModel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_sceneplugincollection_scene_process_data")
public class CollectionElementProcessDataModel extends BaseModel {
    @DefaultStr
    private String agentName;
    @DefaultStr
    private String data;
    @DefaultInt
    private int status;
}
