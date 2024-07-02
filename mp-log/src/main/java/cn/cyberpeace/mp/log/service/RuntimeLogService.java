package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.log.converter.RunTimeLogDTOConverter;
import cn.cyberpeace.mp.log.dto.LogReportDTO;
import cn.cyberpeace.mp.log.dto.RunTimeLogDTO;
import cn.cyberpeace.mp.log.request.BasePageRequest;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName RuntimeLogService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RuntimeLogService {

    private final LogFileServiceFactory logFileServiceFactory;

    private final RunTimeLogDTOConverter runTimeLogDTOConverter;

    /**
     * 分页查询
     *
     * @param basePageRequest
     * @return
     */
    public ListObjResult<RunTimeLogDTO> page(BasePageRequest basePageRequest) {
        List<LogReportDTO> allLogReportDTOList = Lists.newArrayList();
        List<LogReportDTO> mpAndCpLogList = logFileServiceFactory.listAll(basePageRequest);
        allLogReportDTOList.addAll(mpAndCpLogList);
        // 时间过滤
        if (CollectionUtils.isNotEmpty(allLogReportDTOList)) {
            if (Objects.nonNull(basePageRequest.getStartDate()) && Objects.nonNull(basePageRequest.getEndDate())) {
                List<LogReportDTO> list = new ArrayList<>();
                for (LogReportDTO logReportDTO : allLogReportDTOList) {
                    if (logReportDTO.getUpdateTime().isAfter(basePageRequest.getStartDate())
                            && basePageRequest.getEndDate().isAfter(logReportDTO.getUpdateTime())) {
                        list.add(logReportDTO);
                    }
                }
                allLogReportDTOList = list;
            }

            allLogReportDTOList.sort(Comparator.comparing(LogReportDTO::getLogReportTypeEnum).thenComparing(LogReportDTO::getUpdateTime).reversed());
        }

        List<RunTimeLogDTO> runTimeLogDTOS = runTimeLogDTOConverter.targetToSource(allLogReportDTOList);
        // 添加临时返回id
        if (CollectionUtils.isNotEmpty(runTimeLogDTOS)) {
            for (RunTimeLogDTO dto : runTimeLogDTOS) {
                dto.setId(TextUtils.uuid());
            }
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
