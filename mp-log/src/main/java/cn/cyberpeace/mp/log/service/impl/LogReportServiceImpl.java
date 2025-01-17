 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogReportServiceImpl.java.
 // File size 2419.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
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
  * 日志报告服务impl
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Slf4j
@Service
@RequiredArgsConstructor
public class LogReportServiceImpl implements LogReportService {

    private final LogReportMapper logReportMapper;

    private final LogReportDTOConverter logReportDTOConverter;

     /**
      * 报告
      *
      * @param logReportTypeEnum 日志报表类型enum
      * @param list              列表
      * @return int
      */
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

     /**
      * 列表
      *
      * @param type 类型
      * @return {@link List }<{@link LogReportDTO }>
      */
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
