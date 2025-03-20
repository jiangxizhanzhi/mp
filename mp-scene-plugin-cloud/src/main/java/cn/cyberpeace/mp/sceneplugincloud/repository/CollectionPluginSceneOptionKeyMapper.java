 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectionPluginSceneOptionKeyMapper.java.
 // File size 735.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.sceneplugincloud.model.CollectionPluginSceneOptionKeyModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName CollectionPluginSceneOptionKeyMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelMapper
 */
@Mapper
@Repository
@ModelClass(CollectionPluginSceneOptionKeyModel.class)
public interface CollectionPluginSceneOptionKeyMapper extends BaseModelMapper<CollectionPluginSceneOptionKeyModel> {
}
