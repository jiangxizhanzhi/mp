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
        log.info("delete history log report type:{}, num：{}", logReportTypeEnum, delete);
        List<LogReportPO> logReportPOS = logReportDTOConverter.sourceToTarget(list);
        logReportPOS.forEach(v -> v.setType(logReportTypeEnum));
        return logReportMapper.bulkCreate(logReportPOS).getSuccessCount();
    }

    @Override
    public List<LogReportDTO> list(String type) {
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

    @Override
    public Integer delete(String type, List<String> list) {
        HumanCondition humanCondition = new HumanCondition();
        if (StringUtils.isNotBlank(type)) {
            humanCondition.filterEqual("type", type);
        }
        if (!list.isEmpty()) {
            String[] fileNameArray = list.toArray(new String[0]);
            humanCondition.filterIn(LogReportPO::getFileName, fileNameArray);
        }
        List<LogReportPO> delete_list = logReportMapper.list(humanCondition);

        String[] ids = new String[delete_list.size()];
        // 遍历delete_list，并将ID存储在数组中
        for (int i = 0; i < delete_list.size(); i++) {
            // 获取每个LogReportPO对象的ID
            ids[i] = delete_list.get(i).getId();
        }
        return logReportMapper.bulkDelete(ids).getSuccessCount();
    }
}
