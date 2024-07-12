 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudPluginSceneElementMapper.java.
 // File size 742.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName CloudPluginSceneElementMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePluginSceneElementMapper
 */
@Mapper
@Repository
@ModelClass(CloudPluginSceneElementModel.class)
public interface CloudPluginSceneElementMapper extends BasePluginSceneElementMapper<CloudPluginSceneElementModel> {
}
