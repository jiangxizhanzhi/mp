package cn.cyberpeace.mp.devopslog.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.service.OptionService;
import cn.cyberpeace.mp.devopslog.bo.LogFileDownloadBO;
import cn.cyberpeace.mp.devopslog.constant.LogConstant;
import cn.cyberpeace.mp.devopslog.dto.LogDownloadDTO;
import cn.cyberpeace.mp.devopslog.dto.LogReportDTO;
import cn.cyberpeace.mp.devopslog.enums.LogReportTypeEnum;
import cn.cyberpeace.mp.devopslog.request.BasePageRequest;
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

/**
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/15
 */
@Component
@RequiredArgsConstructor
public class LogFileServiceFactory {

    private final List<LogFileService> fileServiceList;

    private Map<String, LogFileService> logFileServiceMap = new ConcurrentHashMap();

    /**
     * 查询全部日志上报数据
     * @return
     */
    public List<LogReportDTO> listAll(BasePageRequest basePageRequest) {
        return new ArrayList<>();
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
        if (Objects.isNull(logFileService)) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        return logFileService.download(downloadDTO);
    }
}
