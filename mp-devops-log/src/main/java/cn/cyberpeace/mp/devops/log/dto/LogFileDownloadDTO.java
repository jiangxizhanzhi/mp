package cn.cyberpeace.mp.devops.log.dto;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/17
 */
@Data
public class LogFileDownloadDTO {

    /**
     * 文件名称
     */
    private List<String> logFileNames;
}
