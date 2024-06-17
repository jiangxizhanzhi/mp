package cn.cyberpeace.mp.scenepluginconfig.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scenepluginconfig.model.PluginLinkAgentType;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@ModelClass(PluginLinkAgentType.class)
public interface PluginLinkAgentTypeMapperV2 extends BaseModelMapper<PluginLinkAgentType> {

}
