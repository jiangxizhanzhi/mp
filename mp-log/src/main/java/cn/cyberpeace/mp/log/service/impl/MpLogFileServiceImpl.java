package cn.cyberpeace.mp.log.service.impl;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.log.bo.LogFileDownloadBO;
import cn.cyberpeace.mp.log.config.LogConfig;
import cn.cyberpeace.mp.log.constant.ControllerConstant;
import cn.cyberpeace.mp.log.constant.LogErrorCode;
import cn.cyberpeace.mp.log.dto.LogDownloadDTO;
import cn.cyberpeace.mp.log.dto.LogReportDTO;
import cn.cyberpeace.mp.log.enums.LogReportTypeEnum;
import cn.cyberpeace.mp.log.service.LogFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
@Slf4j
public class MpLogFileServiceImpl implements LogFileService {

    @Value("${logging.file.path}")
    private String logPath;

    @Autowired
    private LogConfig logConfig;

    @Override
    public String logReportType() {
        return LogReportTypeEnum.MP.toValue();
    }

    @Override
    public List<LogReportDTO> list(String type) {
        var logFile = new File(logPath);
        List<File> fileList = Arrays.stream(Objects.requireNonNull(logFile.listFiles())).filter(File::isFile).collect(Collectors.toList());
        List<LogReportDTO> logReportDTOList = fileList.stream().map(f -> {
            LogReportDTO dto = new LogReportDTO();
            String fileName = f.getName();
            if (!fileName.startsWith(LogReportTypeEnum.MP.getPrefix())) {
                fileName = LogReportTypeEnum.MP.getPrefix() + fileName;
            }
            dto.setFileName(fileName);
            dto.setFileSize(f.length());
            dto.setLogReportTypeEnum(LogReportTypeEnum.MP);
            dto.setDownloadUrl(ControllerConstant.BASE_API_PATH + "/runtime_log/download?fileName=" + f.getName() + "&type=" + LogReportTypeEnum.MP.toValue());
            dto.setHostIp(logConfig.getMpIp());
            try {
                dto.setUpdateTime(Files.getLastModifiedTime(f.toPath()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                if (fileName.endsWith(".zip")) {
                    dto.setUpdateTime(dto.getUpdateTime().minusDays(1L).withHour(23).withMinute(59));
                }
            } catch (IOException e) {
                dto.setUpdateTime(LocalDateTime.now());
            }
            return dto;
        }).collect(Collectors.toList());
        return logReportDTOList;
    }


    @Override
    public LogFileDownloadBO download(LogDownloadDTO downloadDTO) throws IOException{
        String fileName = downloadDTO.getFileName();
        var bos = new ByteArrayOutputStream();
        var logFile = new File(logPath);
        List<File> fileList = Arrays.stream(Objects.requireNonNull(logFile.listFiles())).filter(File::isFile).collect(Collectors.toList());
        List<File> downloadFiles = fileList.stream().filter(file -> fileName.equals(file.getName())).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(downloadFiles)) {
            log.error("download file is empty for {}", fileName);
            throw new MpRuntimeException(LogErrorCode.DOWNLOAD_LOG_FILE_ERROR);
        }

        if (!fileName.endsWith(".zip")) {
            var zout = new ZipOutputStream(bos);
            for (File file : downloadFiles) {
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    ZipEntry zipFile = new ZipEntry(file.getName());
                    zout.putNextEntry(zipFile);
                    byte[] tmp = new byte[4 * 1024];
                    int size = 0;
                    while ((size = fileInputStream.read(tmp)) != -1) {
                        zout.write(tmp, 0, size);
                    }
                    zout.flush();
                    zout.closeEntry();
                }
            }
            zout.finish();
        } else {
            try (InputStream inputStream = new FileInputStream(downloadFiles.get(0))) {
                byte[] tmp = new byte[4 * 1024];
                int size = 0;
                while ((size = inputStream.read(tmp)) != -1) {
                    bos.write(tmp, 0, size);
                }
                bos.flush();
            }
        }

        String downLoadFileName = !fileName.endsWith(".zip") ? fileName.concat(".zip") : fileName;
        if (!downLoadFileName.startsWith(LogReportTypeEnum.MP.getPrefix())) {
            downLoadFileName = LogReportTypeEnum.MP.getPrefix() + downLoadFileName;
        }

        return LogFileDownloadBO.builder().inputStream(new ByteArrayInputStream(bos.toByteArray())).downLoadFileName(downLoadFileName).build();
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
