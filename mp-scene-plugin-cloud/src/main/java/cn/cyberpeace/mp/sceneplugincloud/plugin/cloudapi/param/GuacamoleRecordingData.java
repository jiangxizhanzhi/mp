package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuacamoleRecordingData implements CloudHttpData {
    private List<RecordingInfo> data;
    private Integer totalnum;
    private Integer totalpage;
    private Boolean success;
    private String requestId;

    private String message;

    @Data
    public static class RecordingInfo {
        private String id;
        private String uuid;
        private String createdAt;
        private String updatedAt;
        private Boolean deleted;
        private String deletedAt;
        private String user;
        private String project;
        private String connectionId;
        private String recordingName;
        private String username;
        private String department;
        private String scene;
        private String recordingFile;
        private String fileSize;
        private String recordate;
        private String recortime;
    }
}
