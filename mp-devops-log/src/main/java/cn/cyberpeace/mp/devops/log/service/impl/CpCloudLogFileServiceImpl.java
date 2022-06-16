package cn.cyberpeace.mp.devops.log.service.impl;

import cn.cyberpeace.mp.devops.log.dto.LogReportDTO;
import cn.cyberpeace.mp.devops.log.enums.LogReportTypeEnum;
import cn.cyberpeace.mp.devops.log.service.LogFileService;
import org.apache.http.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 云平台日志模块
 *
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/15
 */
@Service
public class CpCloudLogFileServiceImpl extends BaseOpenStackHttpService implements LogFileService {

    @Override
    public String logReportType() {
        return LogReportTypeEnum.CP.toValue();
    }


    @Override
    public List<LogReportDTO> list() {
        return null;
    }

    @Override
    public ByteArrayInputStream download() {
        HttpResponse httpResponse = super.downLoad("");
        Header contentHeader = httpResponse.getFirstHeader("Content-Disposition");
        HeaderElement[] values = contentHeader.getElements();
        NameValuePair filename = values[0].getParameterByName("filename");
        String fileName = new String(filename.getValue().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        HttpEntity entity = httpResponse.getEntity();
        var bos = new ByteArrayOutputStream();
        try {
            InputStream inputStream = entity.getContent();
            var zout = new ZipOutputStream(bos);
            ZipEntry zipFile = new ZipEntry(fileName);
            zout.putNextEntry(zipFile);
            byte[] tmp = new byte[4 * 1024];
            int size = 0;
            while ((size = inputStream.read(tmp)) != -1) {
                zout.write(tmp, 0, size);
            }
            zout.flush();
            zout.closeEntry();

            inputStream.close();
            zout.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(bos.toByteArray());
    }

    @Override
    public ByteArrayInputStream download(List<String> logFileNames) {
        return null;
    }
}
