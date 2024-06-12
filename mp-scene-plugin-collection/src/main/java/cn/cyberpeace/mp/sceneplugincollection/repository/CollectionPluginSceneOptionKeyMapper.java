package cn.cyberpeace.mp.sceneplugincollection.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneOptionKeyModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(CollectionPluginSceneOptionKeyModel.class)
public interface CollectionPluginSceneOptionKeyMapper extends BaseModelMapper<CollectionPluginSceneOptionKeyModel> {
}
