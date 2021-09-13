package cn.cyberpeace.mp.devops.log.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.devops.log.request.BasePageRequest;
import cn.cyberpeace.mp.devops.log.response.RunTimeLogResponse;
import com.google.common.collect.Lists;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 **/
@Service
@Slf4j
public class RuntimeLogService {

    @Value("${logging.file.path}")
    private String logPath;

    /**
     * 查询运行时日志文件分页列表
     *
     * @param basePageRequest basePageRequest
     * @return ListObjResult<RunTimeLogResponse>
     */
    public ListObjResult<RunTimeLogResponse> queryRuntimeLog(BasePageRequest basePageRequest) {
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

        //process page
        int offset = (basePageRequest.getPage() - 1) * basePageRequest.getPerPage();
        if (offset < logResponseList.size()) {
            int toIndex = Math.min(offset + basePageRequest.getPerPage(), logResponseList.size());
            return new ListObjResult<>(logResponseList.subList(offset, toIndex), logResponseList.size());
        } else {
            return new ListObjResult<>(Lists.newArrayList(), logResponseList.size());
        }
    }

    /**
     * 下载压缩文件
     * @param logFileNames logFileNames
     * @return ByteArrayInputStream
     */
    public ByteArrayInputStream zipDownLoadFile(List<String> logFileNames){
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
                byte[] tmp = new byte[4*1024];
                int size = 0;
                while((size = fileInputStream.read(tmp)) != -1){
                    zout.write(tmp, 0, size);
                }
                zout.flush();
                zout.closeEntry();
                fileInputStream.close();
            }
        }catch (Exception e){
            log.error("file read path fail");
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }


        return new ByteArrayInputStream(bos.toByteArray());


    }

}
