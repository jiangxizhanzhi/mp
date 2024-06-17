package cn.cyberpeace.mp.scenepluginconfig.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneElementMapper;
import cn.cyberpeace.mp.scenepluginconfig.model.ConfigPluginSceneConfigElementModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(ConfigPluginSceneConfigElementModel.class)
public interface ConfigPluginSceneConfigElementMapper
        extends BasePluginSceneElementMapper<ConfigPluginSceneConfigElementModel> {
}
