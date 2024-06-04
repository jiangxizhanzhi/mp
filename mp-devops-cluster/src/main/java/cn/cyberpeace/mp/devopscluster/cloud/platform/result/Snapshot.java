package cn.cyberpeace.mp.devopscluster.cloud.platform.result;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
