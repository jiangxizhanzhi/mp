 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogDownloadDTO.java.
 // File size 847.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.dto;

import cn.cyberpeace.mp.log.request.RunTimeLogDownloadRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName LogDownloadDTO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
public class LogDownloadDTO {

    /**
     * 类型
     */
    private String type;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 节点名称
     */
    private String hostname;

    /**
     * 路径
     */
    private String path;

    public LogDownloadDTO(RunTimeLogDownloadRequest request){
        this.type = request.getType();
        this.fileName = request.getFileName();
        this.hostname = request.getHostname();
        this.path = request.getPath();
    }
}
