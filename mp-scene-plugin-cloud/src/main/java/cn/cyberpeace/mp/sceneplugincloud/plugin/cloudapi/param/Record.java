package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.Data;

/**
 * @ClassName Record
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class Record {
    private String connectionId;
    private String username;
    private String department;
    private String scene;
    private String recordingFile;
    private String fileSize;
    private String recordate;
    private String recortime;
}
