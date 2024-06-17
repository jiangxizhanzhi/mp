package cn.cyberpeace.mp.scenepluginconfig.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneElementMapper;
import cn.cyberpeace.mp.scenepluginconfig.model.ConfigPluginSceneElementModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(ConfigPluginSceneElementModel.class)
public interface ConfigPluginSceneElementMapper extends BasePluginSceneElementMapper<ConfigPluginSceneElementModel> {
}
