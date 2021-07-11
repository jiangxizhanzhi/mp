package cn.cyberpeace.mp.log.service.impl;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.log.bo.LogFileDownloadBO;
import cn.cyberpeace.mp.log.constant.ControllerConstant;
import cn.cyberpeace.mp.log.constant.LogErrorCode;
import cn.cyberpeace.mp.log.dto.LogDownloadDTO;
import cn.cyberpeace.mp.log.dto.LogReportDTO;
import cn.cyberpeace.mp.log.enums.LogReportTypeEnum;
import cn.cyberpeace.mp.log.model.CloudPlatformItemsResult;
import cn.cyberpeace.mp.log.po.CpCloudLogPO;
import cn.cyberpeace.mp.log.service.LogFileService;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
public class CpLogFileServiceImpl extends BaseCpHttpService implements LogFileService {

    
    public static final String CPCLOUD_URL = "/cpcloud";

    private static final String LIST_URL = CPCLOUD_URL + "/logger_app/v1/logfile";

    private static final String DOWNLOAD_URL = CPCLOUD_URL + "/logger_app/v1/download_logfiles";

    @Override
    public String logReportType() {
        return LogReportTypeEnum.CP.toValue();
    }


    @Override
    public List<LogReportDTO> list(String type) {
        CloudPlatformItemsResult t = null;
        try {
            t = super.sendPost(LIST_URL, JSONObject.toJSONString(new LogListParams()), CloudPlatformItemsResult.class);
        } catch (Exception e) {
            log.error("list cp log error, {}", ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(LogErrorCode.REQUEST_CLOUD_INTERFACE_BUSINESS_ERROR.format("CP Service Error"));
        }
        if (Objects.isNull(t)) {
            throw new MpRuntimeException(LogErrorCode.REQUEST_CLOUD_INTERFACE_BUSINESS_ERROR.format("CP Service Error"));
        }
        if (!t.isSuccess()){
            throw new MpRuntimeException(LogErrorCode.REQUEST_CLOUD_INTERFACE_BUSINESS_ERROR.format(t.getMessage()));
        }
        List<CpCloudLogPO> list = JSONObject.parseArray(JSONObject.toJSONString(t.getItems()), CpCloudLogPO.class);
        List<LogReportDTO> reportDTOList = list.stream().map(v -> {
            LogReportDTO dto = new LogReportDTO();
            String fileName = v.getFilename();
            if (!fileName.startsWith(LogReportTypeEnum.CP.getPrefix())) {
                fileName = LogReportTypeEnum.CP.getPrefix() + fileName;
            }
            dto.setHostIp(v.getHostIp());
            dto.setFileName(fileName);
            dto.setFileSize(v.getFileSize());
            dto.setUpdateTime(v.getUpdatetime());
            dto.setLogReportTypeEnum(LogReportTypeEnum.CP);
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
        String downLoadFileName = null;
        ByteArrayOutputStream bos = null;
        InputStream inputStream = null;
        try {
            final cn.hutool.http.HttpResponse httpResponse = super.downLoad(DOWNLOAD_URL, jsonObject.toJSONString());
            String disposition = httpResponse.header(cn.hutool.http.Header.CONTENT_DISPOSITION);
            String fileName = "";
            if (StrUtil.isNotBlank(disposition)) {
                fileName = ReUtil.get("filename=\"(.*?)\"", disposition, 1);
                if (StrUtil.isBlank(fileName)) {
                    fileName = StrUtil.subAfter(disposition, "filename=", true);
                    final int lastIndexOf = fileName.lastIndexOf(";");
                    if (lastIndexOf > 0) {
                        fileName = fileName.substring(0, lastIndexOf);
                    }
                }
            }
            downLoadFileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
            if (!downLoadFileName.startsWith(LogReportTypeEnum.CP.getPrefix())) {
                downLoadFileName = LogReportTypeEnum.CP.getPrefix() + downLoadFileName;
            }
            bos = new ByteArrayOutputStream();

            inputStream = httpResponse.bodyStream();
            byte[] tmp = new byte[4 * 1024];
            int size = 0;
            while ((size = inputStream.read(tmp)) != -1) {
                bos.write(tmp, 0, size);
            }
            bos.flush();
        } catch (IOException e) {
            log.error("download cp log error, {}", ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(LogErrorCode.REQUEST_CLOUD_INTERFACE_BUSINESS_ERROR.format("CP Service Error"));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return LogFileDownloadBO.builder().inputStream(new ByteArrayInputStream(bos.toByteArray())).downLoadFileName(downLoadFileName).build();
    }

    @Override
    public int getOrder() {
        return 30;
    }

    
    @Data
    @NoArgsConstructor
    private class LogListParams{

        private List<String> hosts;
    }
}
