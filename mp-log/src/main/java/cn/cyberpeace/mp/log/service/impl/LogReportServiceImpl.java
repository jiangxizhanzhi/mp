package cn.cyberpeace.mp.log.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.log.converter.LogReportDTOConverter;
import cn.cyberpeace.mp.log.dto.LogReportDTO;
import cn.cyberpeace.mp.log.enums.LogReportTypeEnum;
import cn.cyberpeace.mp.log.po.LogReportPO;
import cn.cyberpeace.mp.log.repository.LogReportMapper;
import cn.cyberpeace.mp.log.service.LogReportService;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


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
        logReportPOS.forEach(v -> v.setType(logReportTypeEnum));
        return logReportMapper.bulkCreate(logReportPOS).getSuccessCount();
    }

    @Override
    public List<LogReportDTO> list(String type){
        HumanCondition humanCondition = new HumanCondition();
        if (StringUtils.isNotBlank(type)) {
            humanCondition.filterEqual("type", type);
        }
        List<LogReportPO> list = logReportMapper.list(humanCondition);
        list.forEach(logReportPO -> {
            String fileName = logReportPO.getFileName();
            LogReportTypeEnum logType = logReportPO.getType();
            if (!fileName.startsWith(logType.getPrefix())) {
                fileName = logType.getPrefix() + fileName;
                logReportPO.setFileName(fileName);
            }
        });
        return logReportDTOConverter.targetToSource(list);
    }
}
