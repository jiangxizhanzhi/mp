 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogReportDTOConverter.java.
 // File size 589.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.converter;

import cn.cyberpeace.mp.log.dto.LogReportDTO;
import cn.cyberpeace.mp.log.po.LogReportPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface LogReportDTOConverter extends IConverter<LogReportDTO, LogReportPO> {

    @Mapping(source = "logReportTypeEnum",target = "type")
    @Override
    LogReportPO sourceToTarget(LogReportDTO source);
}
