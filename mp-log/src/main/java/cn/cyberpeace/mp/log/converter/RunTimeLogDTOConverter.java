 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RunTimeLogDTOConverter.java.
 // File size 893.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.converter;

import cn.cyberpeace.mp.log.dto.LogReportDTO;
import cn.cyberpeace.mp.log.dto.RunTimeLogDTO;
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
