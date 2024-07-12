 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectionPluginWazuhRuleTypeRelationMapper.java.
 // File size 770.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginWazuhRuleTypeRelationModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName CollectionPluginWazuhRuleTypeRelationMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelMapper
 */
@Mapper
@Repository
@ModelClass(CollectionPluginWazuhRuleTypeRelationModel.class)
public interface CollectionPluginWazuhRuleTypeRelationMapper extends BaseModelMapper<CollectionPluginWazuhRuleTypeRelationModel> {
}
