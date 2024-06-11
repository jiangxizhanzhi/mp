package cn.cyberpeace.mp.behavior.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.behavior.model.NetworkBehaviorModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(NetworkBehaviorModel.class)
public interface NetworkBehaviorMapper extends BaseModelMapper<NetworkBehaviorModel> {
}
