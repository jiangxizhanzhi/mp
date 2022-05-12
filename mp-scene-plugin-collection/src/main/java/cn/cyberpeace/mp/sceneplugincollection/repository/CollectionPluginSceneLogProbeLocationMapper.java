package cn.cyberpeace.mp.sceneplugincollection.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneLogProbeLocationModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(CollectionPluginSceneLogProbeLocationModel.class)
public interface CollectionPluginSceneLogProbeLocationMapper extends BaseModelMapper<CollectionPluginSceneLogProbeLocationModel> {
}
