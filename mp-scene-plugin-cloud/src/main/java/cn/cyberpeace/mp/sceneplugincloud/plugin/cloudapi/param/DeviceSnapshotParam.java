package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName DeviceSnapshotParam
 * @Description
 * @Author maojun
 * @Date 2022/7/15
 * @Version 1.0.0
 * @see CloudHttpParam
 */
@Getter
@Setter
public class DeviceSnapshotParam implements CloudHttpParam {
    private String deviceUuid;
    private Integer limit;
    private Integer offset;
}
