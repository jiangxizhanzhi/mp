package cn.cyberpeace.mp.devops.log.service.impl;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.devops.log.bo.LogFileDownloadBO;
import cn.cyberpeace.mp.devops.log.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.log.constant.LogErrorCode;
import cn.cyberpeace.mp.devops.log.dto.LogDownloadDTO;
import cn.cyberpeace.mp.devops.log.dto.LogReportDTO;
import cn.cyberpeace.mp.devops.log.enums.LogReportTypeEnum;
import cn.cyberpeace.mp.devops.log.model.CloudPlatformItemsResult;
import cn.cyberpeace.mp.devops.log.po.CpCloudLogPO;
import cn.cyberpeace.mp.devops.log.service.LogFileService;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 云平台日志模块
 *
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/15
 */
@Service
public class CpLogFileServiceImpl extends BaseCpHttpService implements LogFileService {

    /**
     * cpcloud平台URL前缀
     */
    public static final String CPCLOUD_URL = "/cpcloud";

    private static final String LIST_URL = CPCLOUD_URL + "/logger_app/v1/logfile";

    private static final String DOWNLOAD_URL = CPCLOUD_URL + "/logger_app/v1/download_logfiles";

    @Override
    public String logReportType() {
        return LogReportTypeEnum.CP.toValue();
    }


    @Override
    public List<LogReportDTO> list() {
        CloudPlatformItemsResult t = super.sendPost(LIST_URL,JSONObject.toJSONString(new LogListParams()), CloudPlatformItemsResult.class);
        if (Objects.isNull(t) || !t.isSuccess() ){
            throw new MpRuntimeException(LogErrorCode.REQUEST_CLOUD_INTERFACE_BUSINESS_ERROR.format(t.getMessage()));
        }
        List<CpCloudLogPO> list = JSONObject.parseArray(JSONObject.toJSONString(t.getItems()), CpCloudLogPO.class);
        List<LogReportDTO> reportDTOList = list.stream().map(v -> {
            LogReportDTO dto = new LogReportDTO();
            dto.setFileName(v.getFilename());
            dto.setFileSize(v.getFileSize());
            dto.setUpdateTime(v.getUpdatetime());
            dto.setLogReportTypeEnum(LogReportTypeEnum.MP);
            dto.setDownloadUrl(ControllerConstant.BASE_API_PATH + "/runtime_log/download?hostname=" + v.getHostname() + "&path=" + v.getPath() + "&fileName=" + v.getFilename() + "&type=" + LogReportTypeEnum.CP.toValue());
            return dto;
        }).collect(Collectors.toList());
        return reportDTOList;
    }

    @Override
    public LogFileDownloadBO download(LogDownloadDTO downloadDTO) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("filename",downloadDTO.getFileName());
        jsonObject.put("path",downloadDTO.getPath());
        jsonObject.put("hostname",downloadDTO.getHostname());
        HttpResponse httpResponse = super.downLoad(DOWNLOAD_URL,jsonObject.toJSONString());
        Header contentHeader = httpResponse.getFirstHeader("Content-Disposition");
        HeaderElement[] values = contentHeader.getElements();
        NameValuePair filename = values[0].getParameterByName("filename");
        String downLoadFileName = new String(filename.getValue().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        HttpEntity entity = httpResponse.getEntity();
        var bos = new ByteArrayOutputStream();
        InputStream inputStream = entity.getContent();
        byte[] tmp = new byte[4 * 1024];
        int size = 0;
        while ((size = inputStream.read(tmp)) != -1) {
            bos.write(tmp, 0, size);
        }
        bos.flush();
        inputStream.close();
        return LogFileDownloadBO.builder().inputStream(new ByteArrayInputStream(bos.toByteArray())).downLoadFileName(downLoadFileName).build();
    }

    /**
     *
     */
    @Data
    @NoArgsConstructor
    private class LogListParams{

        private List<String> hosts;
    }
}
