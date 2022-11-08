package cn.cyberpeace.mp.sceneplugincollection.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneElementPortMapper;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneConfigElementPortModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(CollectionPluginSceneConfigElementPortModel.class)
public interface CollectionPluginSceneConfigElementPortMapper
        extends BasePluginSceneElementPortMapper<CollectionPluginSceneConfigElementPortModel> {
}
