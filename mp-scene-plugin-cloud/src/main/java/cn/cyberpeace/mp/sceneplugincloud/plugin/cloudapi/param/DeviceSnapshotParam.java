package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author maojun
 * @Date 2022/7/15
 */
@Getter
@Setter
public class DeviceSnapshotParam implements CloudHttpParam {
    private String deviceUuid;
}
