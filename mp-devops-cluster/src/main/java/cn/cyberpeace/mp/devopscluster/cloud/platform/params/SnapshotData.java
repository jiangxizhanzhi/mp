package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import lombok.Data;

/**
 * @ClassName SnapshotData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see SnakeHttpData
 */
@Data
public class SnapshotData implements SnakeHttpData {
    private String description;
    private String snapshotName;
}
