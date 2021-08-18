package cn.cyberpeace.mp.scenepluginflow.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneElementPortMapper;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneConfigElementPortModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(FlowPluginSceneConfigElementPortModel.class)
public interface FlowPluginSceneConfigElementPortMapper
        extends BasePluginSceneElementPortMapper<FlowPluginSceneConfigElementPortModel> {
}
