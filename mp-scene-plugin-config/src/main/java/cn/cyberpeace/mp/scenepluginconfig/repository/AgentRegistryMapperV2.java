package cn.cyberpeace.mp.scenepluginconfig.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scenepluginconfig.model.AgentRegistry;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@ModelClass(AgentRegistry.class)
public interface AgentRegistryMapperV2 extends BaseModelMapper<AgentRegistry> {}
