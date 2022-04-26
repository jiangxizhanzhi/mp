package cn.cyberpeace.mp.devops.cluster.vo.convert;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.OpenFlow;
import cn.cyberpeace.mp.devops.cluster.vo.OpenFlowVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OpenFlowVoConverter {

    @Mapping(target = "openFlowMatchConditionVO",ignore = true)
    OpenFlowVO openFlowToOpenFlowVO(OpenFlow openFlow);
}
