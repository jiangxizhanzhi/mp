package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;


@Data
public class VirtualMachineAddPortData implements CloudHttpData {
    private String networkId;
    private String fixedIp;
    private String portId;
}
