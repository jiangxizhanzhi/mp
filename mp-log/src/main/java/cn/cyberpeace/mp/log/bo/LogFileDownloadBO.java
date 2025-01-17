 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogFileDownloadBO.java.
 // File size 643.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.bo;

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
