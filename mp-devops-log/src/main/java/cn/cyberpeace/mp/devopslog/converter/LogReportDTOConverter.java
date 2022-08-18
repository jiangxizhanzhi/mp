package cn.cyberpeace.mp.devopslog.converter;

import cn.cyberpeace.mp.devopslog.dto.LogReportDTO;
import cn.cyberpeace.mp.devopslog.po.LogReportPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface LogReportDTOConverter extends IConverter<LogReportDTO, LogReportPO> {

    @Mapping(source = "logReportTypeEnum",target = "type")
    @Override
    LogReportPO sourceToTarget(LogReportDTO source);
}
