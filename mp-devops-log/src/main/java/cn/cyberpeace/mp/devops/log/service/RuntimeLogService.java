package cn.cyberpeace.mp.devops.log.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.devops.log.converter.RunTimeLogDTOConverter;
import cn.cyberpeace.mp.devops.log.dto.LogReportDTO;
import cn.cyberpeace.mp.devops.log.dto.RunTimeLogDTO;
import cn.cyberpeace.mp.devops.log.request.BasePageRequest;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public ListObjResult<RunTimeLogDTO> page(BasePageRequest basePageRequest){
        List<LogReportDTO> allLogReportDTOList = Lists.newArrayList();

        List<LogReportDTO> mpAndCpLogList = logFileServiceFactory.listAll();
        allLogReportDTOList.addAll(mpAndCpLogList);

        List<RunTimeLogDTO> runTimeLogDTOS = runTimeLogDTOConverter.targetToSource(allLogReportDTOList);
        int offset = (basePageRequest.getPage() - 1) * basePageRequest.getPerPage();
        if (offset < runTimeLogDTOS.size()) {
            int toIndex = Math.min(offset + basePageRequest.getPerPage(), runTimeLogDTOS.size());
            return new ListObjResult<>(runTimeLogDTOS.subList(offset, toIndex), runTimeLogDTOS.size());
        } else {
            return new ListObjResult<>(Lists.newArrayList(), runTimeLogDTOS.size());
        }
    }
}
