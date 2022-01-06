package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnableGuacamoleRecordingResult {
    private DataItem data;
    private String messages;
    private String message;
    private Boolean success;
    private String requestId;

    @Data
    public static class DataItem {
        private String id;
        private String uuid;
        private String createdAt;
        private String updatedAt;
        private String createAt;
        private Boolean deleted;
        private String deletedAt;
        private String user;
        private String project;
        private String connectionId;
        private String recordingName;
        private String username;
        private String department;
        private String scene;
        private String host;
    }
}
