package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.ArkimeFlowPersistenceTaskModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(ArkimeFlowPersistenceTaskModel.class)
public interface ArkimeFlowPersistenceTaskMapper extends BaseModelMapper<ArkimeFlowPersistenceTaskModel> {
}
