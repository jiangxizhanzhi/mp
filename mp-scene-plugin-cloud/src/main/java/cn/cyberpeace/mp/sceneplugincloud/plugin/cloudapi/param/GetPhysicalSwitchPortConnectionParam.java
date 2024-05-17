package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import lombok.Data;

@Data
public class GetPhysicalSwitchPortConnectionParam implements CloudHttpParam {
    private String l1SwitchName;
}

