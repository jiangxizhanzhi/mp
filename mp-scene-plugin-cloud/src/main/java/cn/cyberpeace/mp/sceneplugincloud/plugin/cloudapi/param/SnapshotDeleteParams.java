package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Description
 * @Author maojun
 * @Date 2022/7/8
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SnapshotDeleteParams implements CloudHttpData {
    private String deviceUuid;
    private String imageType;
    private String snapshotUuid;
    private String snapshotName;
}
