package cn.cyberpeace.mp.devops.log.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.devops.log.bo.LogFileDownloadBO;
import cn.cyberpeace.mp.devops.log.dto.LogReportDTO;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/15
 */
@Component
@RequiredArgsConstructor
public class LogFileServiceFactory {

    private final List<LogFileService> fileServiceList;

    private Map<String,LogFileService> logFileServiceMap = new ConcurrentHashMap();

    @PostConstruct
    public void init(){
        logFileServiceMap = fileServiceList.stream().collect(Collectors.toMap(LogFileService::logReportType, Function.identity()));
    }

    /**
     * 查询全部日志上报数据
     * @return
     */
    public List<LogReportDTO> listAll(){
        List<LogReportDTO> result = Lists.newArrayList();
        for (LogFileService logFileService : fileServiceList){
            var list = logFileService.list();
            result.addAll(CollectionUtils.isNotEmpty(list) ? list : Lists.newArrayList());
        }
        return result;
    }

    /**
     * 下载日志
     * @param logReportType
     * @param fileName
     */
    public LogFileDownloadBO downLoad(String logReportType, String fileName){
        LogFileService logFileService = logFileServiceMap.get(logReportType);
        if (Objects.isNull(logFileService)){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        return logFileService.download(fileName);
    }
}
