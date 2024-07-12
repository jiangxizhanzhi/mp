 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectionPluginSceneConfigMapper.java.
 // File size 751.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneMapper;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneConfigModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName CollectionPluginSceneConfigMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePluginSceneMapper
 */
@Mapper
@Repository
@ModelClass(CollectionPluginSceneConfigModel.class)
public interface CollectionPluginSceneConfigMapper extends BasePluginSceneMapper<CollectionPluginSceneConfigModel> {
}
