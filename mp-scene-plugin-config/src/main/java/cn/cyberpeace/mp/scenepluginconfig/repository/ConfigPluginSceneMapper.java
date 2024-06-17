package cn.cyberpeace.mp.scenepluginconfig.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneMapper;
import cn.cyberpeace.mp.scenepluginconfig.model.ConfigPluginSceneModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(ConfigPluginSceneModel.class)
public interface ConfigPluginSceneMapper extends BasePluginSceneMapper<ConfigPluginSceneModel> {
}
