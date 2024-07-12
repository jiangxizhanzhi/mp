 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectionPluginSceneElementMapper.java.
 // File size 777.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneElementMapper;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneElementModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName CollectionPluginSceneElementMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePluginSceneElementMapper
 */
@Mapper
@Repository
@ModelClass(CollectionPluginSceneElementModel.class)
public interface CollectionPluginSceneElementMapper extends BasePluginSceneElementMapper<CollectionPluginSceneElementModel> {
}
