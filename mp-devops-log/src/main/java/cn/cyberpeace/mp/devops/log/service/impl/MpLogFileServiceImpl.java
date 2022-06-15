package cn.cyberpeace.mp.devops.log.service.impl;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.log.response.RunTimeLogResponse;
import cn.cyberpeace.mp.devops.log.service.LogFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * MP中台日志模块
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/15
 */
@Service
@Slf4j
public class MpLogFileServiceImpl implements LogFileService {

    @Value("${logging.file.path}")
    private String logPath;

    @Override
    public ServiceResult list() {
        var logFile = new File(logPath);
        List<File> fileList = Arrays.stream(Objects.requireNonNull(logFile.listFiles())).filter(File::isFile).collect(Collectors.toList());
        List<RunTimeLogResponse> logResponseList = new ArrayList<>();

        fileList.forEach(f -> {
            try {
                logResponseList.add(RunTimeLogResponse.builder()
                        .logName(f.getName())
                        .lastModifyTime(Files.getLastModifiedTime(f.toPath()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                        .size(f.length()).build());
            } catch (Exception e) {
                log.error("file read path fail");
                throw new MpRuntimeException(BaseErrorCode.ERROR);
            }
        });
        return ServiceResult.successful();
    }

    @Override
    public ByteArrayInputStream download() {
        return null;
    }

    @Override
    public ByteArrayInputStream download(List<String> logFileNames) {
        var bos = new ByteArrayOutputStream();
        var logFile = new File(logPath);
        List<File> fileList = Arrays.stream(Objects.requireNonNull(logFile.listFiles())).filter(File::isFile).collect(Collectors.toList());
        List<File> downloadFiles = fileList.stream().filter(file -> logFileNames.contains(file.getName())).collect(Collectors.toList());

        var zout = new ZipOutputStream(bos);
        try {
            for (File file : downloadFiles) {
                FileInputStream fileInputStream = new FileInputStream(file);
                ZipEntry zipFile = new ZipEntry(file.getName());
                zout.putNextEntry(zipFile);
                byte[] tmp = new byte[4 * 1024];
                int size = 0;
                while ((size = fileInputStream.read(tmp)) != -1) {
                    zout.write(tmp, 0, size);
                }
                zout.flush();
                zout.closeEntry();

                fileInputStream.close();
            }
            zout.finish();
        } catch (Exception e) {
            log.error("file read path fail");
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }

        return new ByteArrayInputStream(bos.toByteArray());
    }
}
