package cn.cyberpeace.mp.log.dto;

import cn.cyberpeace.mp.log.request.RunTimeLogDownloadRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/15
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
