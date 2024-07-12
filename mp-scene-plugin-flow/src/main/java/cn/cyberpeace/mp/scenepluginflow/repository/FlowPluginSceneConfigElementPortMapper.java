 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowPluginSceneConfigElementPortMapper.java.
 // File size 624.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneElementPortMapper;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneConfigElementPortModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(FlowPluginSceneConfigElementPortModel.class)
public interface FlowPluginSceneConfigElementPortMapper
        extends BasePluginSceneElementPortMapper<FlowPluginSceneConfigElementPortModel> {
}
