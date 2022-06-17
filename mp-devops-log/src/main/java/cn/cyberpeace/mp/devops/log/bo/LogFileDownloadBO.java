package cn.cyberpeace.mp.devops.log.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.ByteArrayInputStream;

/**
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogFileDownloadBO {

    /**
     * 文件流
     */
    private ByteArrayInputStream inputStream;

    /**
     * 文件名称
     */
    private String downLoadFileName;

    /**
     * 下载包类型
     */
    @Builder.Default
    private String contentType = "application/zip";
}
