package cn.cyberpeace.mp.devops.cluster.vo.convert;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.OpenFlow;
import cn.cyberpeace.mp.devops.cluster.vo.OpenFlowVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface OpenFlowVoConverter extends IConverter<OpenFlow, OpenFlowVO>{

    @Mapping(target = "openFlowMatchConditionVO",ignore = true)
    @Override
    OpenFlowVO sourceToTarget(OpenFlow openFlow);
}
