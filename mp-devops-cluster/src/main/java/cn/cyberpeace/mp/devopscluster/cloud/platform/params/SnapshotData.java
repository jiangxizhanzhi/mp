package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import lombok.Data;

@Data
public class SnapshotData implements SnakeHttpData {
    private String description;
    private String snapshotName;
}
