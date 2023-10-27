package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
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

    @javax.annotation.Resource(name = "sysOptionServiceImpl")
    private OptionService optionService;

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
            var res = optionService.getStrOptions(LogConstant.MP_COMMON_OPTION_TYPE_RUNTIME_LOG_TYPE);
            List<OptionBO> options = res.getSource();
            List<String> codes = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(options)) {
                codes = options.stream().map(o -> (String) o.getCode()).collect(Collectors.toList());
            }

            Set<String> codeSet = new HashSet<>(codes);
            List<CompletableFuture<List<LogReportDTO>>> logReportFinalFutureList = new ArrayList<>();

            List<CompletableFuture<List<LogReportDTO>>> logReportFutureList = fileServiceList.stream()
                    .filter(logFileService -> isReportTypeMatch(logFileService.logReportType(), codeSet))
                    .map(logFileService -> CompletableFuture.supplyAsync(() -> logFileService.list(null),
                            TaskCommon.taskCommonPool.getExecutorService())).collect(Collectors.toList());
            logReportFinalFutureList.addAll(logReportFutureList);


            var logservice = logFileServiceMap.get(LogReportTypeEnum.REPORT.name());
            List<CompletableFuture<List<LogReportDTO>>> reportLogReportFutureList = codeSet.stream().map(code ->
                    CompletableFuture.supplyAsync(() -> logservice.list(code), TaskCommon.taskCommonPool.getExecutorService())).collect(Collectors.toList());
            logReportFinalFutureList.addAll(reportLogReportFutureList);

            result.addAll(
                    logReportFinalFutureList.stream().map(CompletableFuture::join).flatMap(List::stream).collect(Collectors.toList()));
        }

        return result;
    }

    private boolean isReportTypeMatch(String reportType, Set<String> codes) {
        if (codes.contains(reportType)) {
            return true;
        }
        return false;
    }

    /**
     * 下载日志
     * @param downloadDTO
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
