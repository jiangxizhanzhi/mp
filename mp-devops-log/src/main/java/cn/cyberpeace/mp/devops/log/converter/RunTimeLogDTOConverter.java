package cn.cyberpeace.mp.devops.log.converter;

import cn.cyberpeace.mp.devops.log.dto.LogReportDTO;
import cn.cyberpeace.mp.devops.log.dto.RunTimeLogDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface RunTimeLogDTOConverter extends IConverter<RunTimeLogDTO, LogReportDTO> {

    @Mappings({
            @Mapping(source = "logName",target = "fileName"),
            @Mapping(source = "size",target = "fileSize"),
            @Mapping(source = "downloadUrl",target = "downloadUrl"),
            @Mapping(source = "lastModifyTime",target = "updateTime",dateFormat = "yyyy-MM-dd HH:mm:ss")
    })
    @Override
    LogReportDTO sourceToTarget(RunTimeLogDTO source);
}
