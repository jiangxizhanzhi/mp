 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogFileServiceFactory.java.
 // File size 5726.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.thread.taskpool.DefaultResultPoolTask;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.service.OptionService;
import cn.cyberpeace.mp.log.bo.LogFileDownloadBO;
import cn.cyberpeace.mp.log.constant.LogConstant;
import cn.cyberpeace.mp.log.dto.LogDownloadDTO;
import cn.cyberpeace.mp.log.dto.LogReportDTO;
import cn.cyberpeace.mp.log.enums.LogReportTypeEnum;
import cn.cyberpeace.mp.log.request.BasePageRequest;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @ClassName LogFileServiceFactory
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Component
@RequiredArgsConstructor
public class LogFileServiceFactory {

    @javax.annotation.Resource(name = "sysOptionServiceImpl")
    private OptionService optionService;

    private final List<LogFileService> fileServiceList;

    private Map<String,LogFileService> logFileServiceMap = new ConcurrentHashMap();

    /**
     * init
     */
    @PostConstruct
    public void init() {
        Map<String, LogFileService> map = new HashMap<>();
        for (LogFileService logFileService : fileServiceList) {
            if (map.put(logFileService.logReportType(), logFileService) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        logFileServiceMap = map;
    }

    /**
     * 查询全部日志上报数据
     *
     * @param basePageRequest
     * @return
     */
    public List<LogReportDTO> listAll(BasePageRequest basePageRequest) {
        List<LogReportDTO> result = Lists.newArrayList();
        if (Objects.nonNull(basePageRequest.getType())) {
            LogFileService logFileService = logFileServiceMap.get(basePageRequest.getType().name());
            if (Objects.isNull(logFileService)) {
                result.addAll(logFileServiceMap.get(LogReportTypeEnum.REPORT.name()).list(basePageRequest.getType().name()));
            } else {
                result.addAll(logFileService.list(basePageRequest.getType().name()));
            }
        } else {
            ServiceResult<List<OptionBO>> res = optionService.getStrOptions(LogConstant.MP_COMMON_OPTION_TYPE_RUNTIME_LOG_TYPE);
            List<OptionBO> options = res.getSource();
            List<String> codes = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(options)) {
                codes = options.stream().map(o -> (String) o.getCode()).collect(Collectors.toList());
            }

            Set<String> codeSet = new HashSet<>(codes);

            List<Callable<List<LogReportDTO>>> totalLogReportCallableList = new ArrayList<>();
            List<Callable<List<LogReportDTO>>> logReportCallableList = new ArrayList<>();
            for (LogFileService logFileService : fileServiceList) {
                if (isReportTypeMatch(logFileService.logReportType(), codeSet)) {
                    Callable<List<LogReportDTO>> listCallable = () -> logFileService.list(null);
                    logReportCallableList.add(listCallable);
                }
            }

            LogFileService logservice = logFileServiceMap.get(LogReportTypeEnum.REPORT.name());
            List<Callable<List<LogReportDTO>>> reportLogReportCallableList = new ArrayList<>();
            for (String code : codeSet) {
                Callable<List<LogReportDTO>> listCallable = () -> logservice.list(code);
                reportLogReportCallableList.add(listCallable);
            }

            totalLogReportCallableList.addAll(logReportCallableList);
            totalLogReportCallableList.addAll(reportLogReportCallableList);

            List<LogReportDTO> totalLogReportList = new ArrayList<>();
            for (DefaultResultPoolTask<List<LogReportDTO>> base : TaskCommon.callTasks(totalLogReportCallableList)) {
                List<LogReportDTO> logReportDTOS = base.getResult();
                totalLogReportList.addAll(logReportDTOS);
            }
            result.addAll(totalLogReportList);
        }

        return result;
    }

    /**
     * isReportTypeMatch
     *
     * @param reportType
     * @param codes
     * @return boolean
     */
    private boolean isReportTypeMatch(String reportType, Set<String> codes) {
        if (codes.contains(reportType)) {
            return true;
        }
        return false;
    }

    /**
     * 下载日志
     *
     * @param downloadDTO
     * @return
     */
    public LogFileDownloadBO downLoad(LogDownloadDTO downloadDTO) throws IOException {
        String logReportType = downloadDTO.getType();
        LogFileService logFileService = logFileServiceMap.get(logReportType);
        if (Objects.isNull(logFileService)){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        return logFileService.download(downloadDTO);
    }
}
