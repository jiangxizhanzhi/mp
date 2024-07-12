 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogFileDownloadBO.java.
 // File size 689.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.ByteArrayInputStream;

/**
 * @ClassName LogFileDownloadBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
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
