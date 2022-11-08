package cn.cyberpeace.mp.sceneplugincollection.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginWazuhRuleTypeModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(CollectionPluginWazuhRuleTypeModel.class)
public interface CollectionPluginWazuhRuleTypeMapper extends BaseModelMapper<CollectionPluginWazuhRuleTypeModel> {
}
