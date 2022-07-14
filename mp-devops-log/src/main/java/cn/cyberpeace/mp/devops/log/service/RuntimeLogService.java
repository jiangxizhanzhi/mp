package cn.cyberpeace.mp.devops.log.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.devops.log.converter.RunTimeLogDTOConverter;
import cn.cyberpeace.mp.devops.log.dto.LogReportDTO;
import cn.cyberpeace.mp.devops.log.dto.RunTimeLogDTO;
import cn.cyberpeace.mp.devops.log.request.BasePageRequest;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class RuntimeLogService {

    private final LogFileServiceFactory logFileServiceFactory;

    private final RunTimeLogDTOConverter runTimeLogDTOConverter;

    /**
     * 分页查询
     * @param basePageRequest
     * @return
     */
    public ListObjResult<RunTimeLogDTO> page(BasePageRequest basePageRequest) {
        List<LogReportDTO> allLogReportDTOList = Lists.newArrayList();
        List<LogReportDTO> mpAndCpLogList = logFileServiceFactory.listAll(basePageRequest);
        allLogReportDTOList.addAll(mpAndCpLogList);
        // 时间过滤
        if (CollectionUtil.isNotEmpty(allLogReportDTOList) && Objects.nonNull(basePageRequest.getStartDate())
                && Objects.nonNull(basePageRequest.getEndDate())) {
            allLogReportDTOList = allLogReportDTOList.stream().filter(logReportDTO ->
                    logReportDTO.getUpdateTime().isAfter(basePageRequest.getStartDate())
                            && basePageRequest.getEndDate().isAfter(logReportDTO.getUpdateTime()))
                    .collect(Collectors.toList());
        }

        List<RunTimeLogDTO> runTimeLogDTOS = runTimeLogDTOConverter.targetToSource(allLogReportDTOList);
        // 添加临时返回id
        if (CollectionUtil.isNotEmpty(runTimeLogDTOS)) {
            runTimeLogDTOS.forEach(dto -> {
                String md5Id = DigestUtil.md5Hex(dto.getLogReportTypeEnum().name().concat(dto.getLogName()));
                dto.setId(md5Id);
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
