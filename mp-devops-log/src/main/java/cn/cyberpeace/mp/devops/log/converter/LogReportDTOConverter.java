package cn.cyberpeace.mp.devops.log.converter;

import cn.cyberpeace.mp.devops.log.dto.LogReportDTO;
import cn.cyberpeace.mp.devops.log.po.LogReportPO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface LogReportDTOConverter extends IConverter<LogReportDTO, LogReportPO> {

    @Override
    LogReportPO sourceToTarget(LogReportDTO source);
}
