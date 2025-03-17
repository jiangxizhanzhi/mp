package cn.cyberpeace.mp.log.service;

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
import cn.cyberpeace.mp.log.request.RunTimeLogPageRequest;
import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
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

    private final String runTimeLogName = "BC";
    private final List<String> securityLogList = Arrays.asList("MP", "CP");

    /**
     * 查询全部日志上报数据
     * @return
     */
    public List<LogReportDTO> listAll(RunTimeLogPageRequest basePageRequest) {
        List<LogReportDTO> result = Lists.newArrayList();
        if (Objects.nonNull(basePageRequest.getType())) {
            LogFileService logFileService = logFileServiceMap.get(basePageRequest.getType().name());
            //判断是否筛选类型
            if (Objects.nonNull(basePageRequest.getRunTimeLogType()) && basePageRequest.getRunTimeLogType().equals(RunTimeLogPageRequest.runTimeLogFlag)
                    && !runTimeLogName.equals(basePageRequest.getType().name())) {
                return result;
            }
            if (Objects.nonNull(basePageRequest.getRunTimeLogType()) && basePageRequest.getRunTimeLogType().equals(RunTimeLogPageRequest.securityLogFlag)
                    && !securityLogList.contains(basePageRequest.getType().name())) {
                return result;
            }

            if (Objects.isNull(logFileService)) {
                result.addAll(logFileServiceMap.get(LogReportTypeEnum.REPORT.name()).list(basePageRequest.getType().name()));
            } else {
                result.addAll(logFileService.list(basePageRequest.getType().name()));
            }
        } else {
            var res = optionService.getStrOptions(LogConstant.MP_COMMON_OPTION_TYPE_RUNTIME_LOG_TYPE);
            List<OptionBO> options = res.getSource();
            List<String> codes = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(options)) {
                //日志类型筛选
                if (Objects.nonNull(basePageRequest.getRunTimeLogType()) && basePageRequest.getRunTimeLogType().equals(RunTimeLogPageRequest.runTimeLogFlag)) {
                    codes = options.stream().map(o -> (String) o.getCode()).filter(runTimeLogName::equals).collect(Collectors.toList());
                } else if (Objects.nonNull(basePageRequest.getRunTimeLogType()) && basePageRequest.getRunTimeLogType().equals(RunTimeLogPageRequest.securityLogFlag)) {
                    codes = options.stream().map(o -> (String) o.getCode()).filter(securityLogList::contains).collect(Collectors.toList());
                } else {
                    codes = options.stream().map(o -> (String) o.getCode()).collect(Collectors.toList());
                }
            }

            List<LogFileService> logFileServices = new ArrayList<>();
            CollectionUtil.addAll(logFileServices, fileServiceList);
            //processVisibleLogFileServices(logFileServices);

            for (LogFileService logFileService : fileServiceList) {
                if (isReportTypeMatch(logFileService.logReportType(), codes)) {
                    var list = logFileService.list(null);
                    result.addAll(CollectionUtils.isNotEmpty(list) ? list : Lists.newArrayList());
                }
            }

            var logservice = logFileServiceMap.get(LogReportTypeEnum.REPORT.name());
            for (String code : codes) {
                var list = logservice.list(code);
                result.addAll(CollectionUtils.isNotEmpty(list) ? list : Lists.newArrayList());
            }
        }

        return result;
    }

    private boolean isReportTypeMatch(String reportType, List<String> codes) {
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
