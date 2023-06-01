package cn.cyberpeace.mp.sceneplugincollection.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneDataModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(CollectionPluginSceneDataModel.class)
public interface CollectionPluginSceneDataMapper extends BaseModelMapper<CollectionPluginSceneDataModel> {
}
