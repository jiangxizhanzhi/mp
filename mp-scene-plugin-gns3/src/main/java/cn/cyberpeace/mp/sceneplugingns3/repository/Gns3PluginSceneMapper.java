package cn.cyberpeace.mp.sceneplugingns3.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneMapper;
import cn.cyberpeace.mp.sceneplugingns3.model.Gns3PluginSceneModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(Gns3PluginSceneModel.class)
public interface Gns3PluginSceneMapper extends BasePluginSceneMapper<Gns3PluginSceneModel> {
}
