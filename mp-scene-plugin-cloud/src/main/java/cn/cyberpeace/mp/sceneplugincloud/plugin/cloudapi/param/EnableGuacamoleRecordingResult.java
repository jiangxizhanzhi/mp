 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name EnableGuacamoleRecordingResult.java.
 // File size 1098.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @ClassName EnableGuacamoleRecordingResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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
