package cn.cyberpeace.mp.sceneplugincloud.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneMapper;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(CloudPluginSceneConfigModel.class)
public interface CloudPluginSceneConfigMapper extends BasePluginSceneMapper<CloudPluginSceneConfigModel> {
}
