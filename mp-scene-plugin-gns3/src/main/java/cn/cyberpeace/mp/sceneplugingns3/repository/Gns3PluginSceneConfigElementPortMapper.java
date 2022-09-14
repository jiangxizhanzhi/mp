package cn.cyberpeace.mp.sceneplugingns3.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneElementPortMapper;
import cn.cyberpeace.mp.sceneplugingns3.model.Gns3PluginSceneConfigElementPortModel;
import cn.cyberpeace.mp.sceneplugingns3.model.Gns3PluginSceneElementPortModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(Gns3PluginSceneConfigElementPortModel.class)
public interface Gns3PluginSceneConfigElementPortMapper
        extends BasePluginSceneElementPortMapper<Gns3PluginSceneConfigElementPortModel> {
}
