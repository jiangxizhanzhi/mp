package cn.cyberpeace.mp.devops.log.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.devops.log.converter.LogReportDTOConverter;
import cn.cyberpeace.mp.devops.log.dto.LogReportDTO;
import cn.cyberpeace.mp.devops.log.enums.LogReportTypeEnum;
import cn.cyberpeace.mp.devops.log.po.LogReportPO;
import cn.cyberpeace.mp.devops.log.repository.LogReportMapper;
import cn.cyberpeace.mp.devops.log.service.LogReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description: 日志文件上报接口
 * @author：Geek Wang
 * @date: 2022/6/15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogReportServiceImpl implements LogReportService {

    private final LogReportMapper logReportMapper;

    private final LogReportDTOConverter logReportDTOConverter;

    @Transactional
    @Override
    public int report(LogReportTypeEnum logReportTypeEnum, List<LogReportDTO> list) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual("type", logReportTypeEnum.toValue());
        int delete = logReportMapper.filterDelete(condition);
        log.info("delete history log report type:{}, num：{}",logReportTypeEnum,delete);
        List<LogReportPO> logReportPOS = logReportDTOConverter.sourceToTarget(list);
        logReportPOS.forEach(v -> v.setType(logReportTypeEnum.toValue()));
        return logReportMapper.bulkCreate(logReportPOS);
    }

    @Override
    public List<LogReportDTO> list(){
        List<LogReportPO> list = logReportMapper.list(new HumanCondition());
        return logReportDTOConverter.targetToSource(list);
    }
}
