package cn.cyberpeace.mp.log.service.impl;

import cn.cyberpeace.mp.log.bo.LogFileDownloadBO;
import cn.cyberpeace.mp.log.dto.LogDownloadDTO;
import cn.cyberpeace.mp.log.dto.LogReportDTO;
import cn.cyberpeace.mp.log.enums.LogReportTypeEnum;
import cn.cyberpeace.mp.log.request.LogFileDeleteRequest;
import cn.cyberpeace.mp.log.service.LogFileService;
import cn.cyberpeace.mp.log.service.LogReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BCLogFileServiceImpl implements LogFileService {

    @Autowired
    private LogReportService logReportService;

    @Override
    public int getOrder() {
        return 50;
    }

    @Value("${logging.file.bc-path}")
    private String logPath;

    @Override
    public String logReportType() {
        return LogReportTypeEnum.BC.toValue();
    }

    /**
     * 列表
     *
     * @return
     */
    public List<LogReportDTO> list(String type) {

        return null;
    }

    /**
     * 下载
     *
     * @param downloadDTO
     * @return
     */
    public LogFileDownloadBO download(LogDownloadDTO downloadDTO) throws IOException {

        return null;
    }


    /**
     * 删除
     *
     * @param file_list
     * @return
     */
    public Integer delete(List<LogFileDeleteRequest> file_list) {
        if (file_list == null || file_list.isEmpty()) {
            return 0;
        }
        List<String> fileNameList = new ArrayList<>();
        File dir = new File(logPath);
        int deletedCount = 0;
        if (dir.exists() && dir.isDirectory()) {
            for (var element : file_list) {
                String fileName = element.getLog_name();
                fileNameList.add(fileName);
                File file = new File(dir, fileName);
                if (file.exists() && file.isFile()) {
                    if (file.delete()) {
                        deletedCount++;
                    }
                } else {
                    log.error("The file {} does not exist or is not a file", file.toString());
                    return deletedCount;
                }
            }
        } else {
            log.error("The directory {} does not exist or is empty", dir.toString());
            return deletedCount;
        }
        logReportService.delete(logReportType(), fileNameList);
        return deletedCount;
    }
}
