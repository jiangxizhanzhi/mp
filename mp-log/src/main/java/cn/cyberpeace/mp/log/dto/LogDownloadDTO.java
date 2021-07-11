package cn.cyberpeace.mp.log.dto;

import cn.cyberpeace.mp.log.request.RunTimeLogDownloadRequest;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class LogDownloadDTO {

    
    private String type;

    
    private String fileName;

    
    private String hostname;

    
    private String path;

    public LogDownloadDTO(RunTimeLogDownloadRequest request){
        this.type = request.getType();
        this.fileName = request.getFileName();
        this.hostname = request.getHostname();
        this.path = request.getPath();
    }
}
