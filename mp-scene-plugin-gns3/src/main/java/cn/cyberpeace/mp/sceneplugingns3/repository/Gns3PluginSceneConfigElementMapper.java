package cn.cyberpeace.mp.sceneplugingns3.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneElementMapper;
import cn.cyberpeace.mp.sceneplugingns3.model.Gns3PluginSceneConfigElementModel;
import cn.cyberpeace.mp.sceneplugingns3.model.Gns3PluginSceneElementModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(Gns3PluginSceneConfigElementModel.class)
public interface Gns3PluginSceneConfigElementMapper
        extends BasePluginSceneElementMapper<Gns3PluginSceneConfigElementModel> {
}
