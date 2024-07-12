 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Snapshot.java.
 // File size 818.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.result;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName Snapshot
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class Snapshot {
    private String id;
    private String description;
    private String createdAt;
    private String status;
    private Integer cpu;
    private Integer definedMemory;
    private Boolean memory;
    private Integer ensuredPhysicalMemory;
    private String name;
    private String uuid;
    private String userSnapshotName;
    private String deviceUuid;
    private String imageName;

    private ExtInfo extInfo;

    @Setter
    @Getter
    public static class ExtInfo {
        private String sceneId;
    }
}
