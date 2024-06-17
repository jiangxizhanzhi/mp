package cn.cyberpeace.mp.scenepluginconfig.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneElementPortMapper;
import cn.cyberpeace.mp.scenepluginconfig.model.ConfigPluginSceneConfigElementPortModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(ConfigPluginSceneConfigElementPortModel.class)
public interface ConfigPluginSceneConfigElementPortMapper
        extends BasePluginSceneElementPortMapper<ConfigPluginSceneConfigElementPortModel> {
}
