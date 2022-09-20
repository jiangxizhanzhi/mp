package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * @Description
 * @Author maojun
 * @Date 2022/7/8
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SnapshotParams implements CloudHttpData {
    private String imageName;
    private String deviceUUid;
    private String instanceUuid;
    private String name;
    private String description;
    private Map<String, Object> extInfo;
}
