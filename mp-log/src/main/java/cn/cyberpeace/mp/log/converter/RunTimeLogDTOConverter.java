 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RunTimeLogDTOConverter.java.
 // File size 1037.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.converter;

import cn.cyberpeace.mp.log.dto.LogReportDTO;
import cn.cyberpeace.mp.log.dto.RunTimeLogDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

/**
 * @ClassName RunTimeLogDTOConverter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see IConverter
 */
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
