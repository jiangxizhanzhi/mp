package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreenrecordPageVO extends BaseCloudPlatformResult {

    private List<Record> record;

    private Integer totalnum;

    private Integer totalpage;

    private String requestId;

    @Data
    public static final class Record implements Serializable {

        
        private String username;

        
        private String department;

        
        private String scene;

        
        private String recordingFile;

        
        private Long fileSize;

        
        private String recordate;

        
        private Double recortime;

        
        private String host;

        
        private String hostId;
    }

}
