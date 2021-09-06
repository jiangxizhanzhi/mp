package cn.cyberpeace.mp.scenepluginflow.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneElementPortMapper;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneElementPortModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(FlowPluginSceneElementPortModel.class)
public interface FlowPluginSceneElementPortMapper
        extends BasePluginSceneElementPortMapper<FlowPluginSceneElementPortModel> {
}
