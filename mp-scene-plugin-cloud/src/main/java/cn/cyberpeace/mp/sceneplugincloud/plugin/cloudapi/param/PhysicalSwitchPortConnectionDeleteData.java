package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

@Data
public class PhysicalSwitchPortConnectionDeleteData implements CloudHttpData {
    private String switchName;
    private String portNo;
}