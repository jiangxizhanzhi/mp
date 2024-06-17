package cn.cyberpeace.mp.scenepluginconfig.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scenepluginconfig.model.ConfigAgentModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(ConfigAgentModel.class)
public interface ConfigAgentMapper extends BaseModelMapper<ConfigAgentModel>  {
}
