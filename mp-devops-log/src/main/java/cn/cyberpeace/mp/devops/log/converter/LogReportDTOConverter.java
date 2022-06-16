package cn.cyberpeace.mp.devops.log.converter;

import cn.cyberpeace.mp.devops.log.dto.LogReportDTO;
import cn.cyberpeace.mp.devops.log.po.LogReportPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface LogReportDTOConverter extends IConverter<LogReportDTO, LogReportPO> {

    @Mapping(source = "logReportTypeEnum",target = "type")
    @Override
    LogReportPO sourceToTarget(LogReportDTO source);
}
