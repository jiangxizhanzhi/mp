 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CpCloudLogPO.java.
 // File size 765.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CpCloudLogPO {

    /**
     * 节点
     */
    private String hostname;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 路径
     */
    private String path;

    /**
     * 节点ip
     */
    private String hostIp;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatetime;
}
