package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.log.converter.RunTimeLogDTOConverter;
import cn.cyberpeace.mp.log.dto.LogReportDTO;
import cn.cyberpeace.mp.log.dto.RunTimeLogDTO;
import cn.cyberpeace.mp.log.request.BasePageRequest;
import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class RuntimeLogService {

    private final LogFileServiceFactory logFileServiceFactory;

    private final RunTimeLogDTOConverter runTimeLogDTOConverter;

    
    public ListObjResult<RunTimeLogDTO> page(BasePageRequest basePageRequest) {
        List<LogReportDTO> allLogReportDTOList = Lists.newArrayList();
        List<LogReportDTO> mpAndCpLogList = logFileServiceFactory.listAll(basePageRequest);
        allLogReportDTOList.addAll(mpAndCpLogList);
        
        if (CollectionUtil.isNotEmpty(allLogReportDTOList)) {
            if (Objects.nonNull(basePageRequest.getStartDate()) && Objects.nonNull(basePageRequest.getEndDate())) {
                allLogReportDTOList = allLogReportDTOList.stream().filter(logReportDTO ->
                        logReportDTO.getUpdateTime().isAfter(basePageRequest.getStartDate())
                                && basePageRequest.getEndDate().isAfter(logReportDTO.getUpdateTime()))
                        .collect(Collectors.toList());
            }

            allLogReportDTOList.sort(Comparator.comparing(LogReportDTO::getLogReportTypeEnum).thenComparing(LogReportDTO::getUpdateTime).reversed());
        }

        List<RunTimeLogDTO> runTimeLogDTOS = runTimeLogDTOConverter.targetToSource(allLogReportDTOList);
        
        if (CollectionUtil.isNotEmpty(runTimeLogDTOS)) {
            runTimeLogDTOS.forEach(dto -> {
                dto.setId(TextUtils.uuid());
            });
        }

        int offset = (basePageRequest.getPage() - 1) * basePageRequest.getPerPage();
        if (offset < runTimeLogDTOS.size()) {
            int toIndex = Math.min(offset + basePageRequest.getPerPage(), runTimeLogDTOS.size());
            return new ListObjResult<>(runTimeLogDTOS.subList(offset, toIndex), runTimeLogDTOS.size());
        } else {
            return new ListObjResult<>(Lists.newArrayList(), runTimeLogDTOS.size());
        }
    }
}
