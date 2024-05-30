package cn.cyberpeace.mp.sceneplugincloud.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementLogicalSwitchModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(CloudPluginSceneElementLogicalSwitchModel.class)
public interface CloudPluginSceneElementLogicalSwitchMapper extends BaseModelMapper<CloudPluginSceneElementLogicalSwitchModel> {
}