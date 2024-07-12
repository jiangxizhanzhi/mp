 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DeviceSnapshotResultData.java.
 // File size 1173.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName DeviceSnapshotResultData
 * @Description
 * @Author maojun
 * @Date 2022/7/15
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceSnapshotResultData implements CloudHttpData {

    private List<Snapshot> items;

    private Boolean success;

    private String requestId;

    private Data data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Snapshot {
        private String uuid;
        private String deviceUuid;
        private String createdAt;
        private String name;
        private String description;
        private String imageName;
        private Boolean deleted;
    }

    @lombok.Data
    public static class Data {
        private List<Snapshot> list;
        private Integer total;
    }
}
