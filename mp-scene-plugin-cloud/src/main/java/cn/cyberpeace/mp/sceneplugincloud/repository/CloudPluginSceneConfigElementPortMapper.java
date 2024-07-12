 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudPluginSceneConfigElementPortMapper.java.
 // File size 813.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneElementPortMapper;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigElementPortModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName CloudPluginSceneConfigElementPortMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePluginSceneElementPortMapper
 */
@Mapper
@Repository
@ModelClass(CloudPluginSceneConfigElementPortModel.class)
public interface CloudPluginSceneConfigElementPortMapper
        extends BasePluginSceneElementPortMapper<CloudPluginSceneConfigElementPortModel> {
}
