package cn.cyberpeace.mp.scenepluginflow.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneElementMapper;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneElementModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(FlowPluginSceneElementModel.class)
public interface FlowPluginSceneElementMapper extends BasePluginSceneElementMapper<FlowPluginSceneElementModel> {
}
