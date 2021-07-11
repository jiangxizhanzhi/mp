package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceSnapshotResultData implements CloudHttpData {

    private List<Snapshot> items;

    private Boolean success;

    private String requestId;

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
}
