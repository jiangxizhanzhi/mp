package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

@Data
public class PhysicalSwitchPortConnectionCreateData implements CloudHttpData {
    private String srcSwitchName;
    private String srcPort;
    private String dstSwitchName;
    private String dstPort;
}