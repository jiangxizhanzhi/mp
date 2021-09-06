package cn.cyberpeace.mp.sceneplugincloud.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.plugin.repository.BasePluginSceneElementPortMapper;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigElementPortModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(CloudPluginSceneConfigElementPortModel.class)
public interface CloudPluginSceneConfigElementPortMapper
        extends BasePluginSceneElementPortMapper<CloudPluginSceneConfigElementPortModel> {
}
