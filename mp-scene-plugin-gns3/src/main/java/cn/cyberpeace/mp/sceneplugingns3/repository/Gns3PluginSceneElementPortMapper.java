package cn.cyberpeace.mp.sceneplugingns3.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneElementPortMapper;
import cn.cyberpeace.mp.sceneplugingns3.model.Gns3PluginSceneElementPortModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(Gns3PluginSceneElementPortModel.class)
public interface Gns3PluginSceneElementPortMapper
        extends BasePluginSceneElementPortMapper<Gns3PluginSceneElementPortModel> {
}
