package cn.cyberpeace.mp.sceneplugincollection.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginWazuhRuleTypeRelationModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(CollectionPluginWazuhRuleTypeRelationModel.class)
public interface CollectionPluginWazuhRuleTypeRelationMapper extends BaseModelMapper<CollectionPluginWazuhRuleTypeRelationModel> {
}
