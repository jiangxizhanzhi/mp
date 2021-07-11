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
import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;


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
            if (CollectionUtil.isNotEmpty(options)) {
                codes = options.stream().map(o -> (String) o.getCode()).collect(Collectors.toList());
            }


            List<LogFileService> logFileServices = new ArrayList<>();
            CollectionUtil.addAll(logFileServices, fileServiceList);
            

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

    
    public LogFileDownloadBO downLoad(LogDownloadDTO downloadDTO) throws IOException {
        String logReportType = downloadDTO.getType();
        LogFileService logFileService = logFileServiceMap.get(logReportType);
        if (Objects.isNull(logFileService)){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        return logFileService.download(downloadDTO);
    }
}
