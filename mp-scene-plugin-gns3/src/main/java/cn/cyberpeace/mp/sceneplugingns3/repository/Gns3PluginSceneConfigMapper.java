package cn.cyberpeace.mp.sceneplugingns3.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneMapper;
import cn.cyberpeace.mp.sceneplugingns3.model.Gns3PluginSceneConfigModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(Gns3PluginSceneConfigModel.class)
public interface Gns3PluginSceneConfigMapper extends BasePluginSceneMapper<Gns3PluginSceneConfigModel> {
}
