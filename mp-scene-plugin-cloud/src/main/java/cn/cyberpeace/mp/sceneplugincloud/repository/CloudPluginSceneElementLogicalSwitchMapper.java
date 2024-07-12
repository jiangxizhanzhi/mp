 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudPluginSceneElementLogicalSwitchMapper.java.
 // File size 753.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementLogicalSwitchModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName CloudPluginSceneElementLogicalSwitchMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelMapper
 */
@Mapper
@Repository
@ModelClass(CloudPluginSceneElementLogicalSwitchModel.class)
public interface CloudPluginSceneElementLogicalSwitchMapper extends BaseModelMapper<CloudPluginSceneElementLogicalSwitchModel> {
}