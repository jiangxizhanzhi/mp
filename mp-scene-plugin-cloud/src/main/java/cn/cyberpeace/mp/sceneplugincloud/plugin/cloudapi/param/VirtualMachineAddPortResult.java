package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualMachineAddPortResult implements CloudHttpData {
    private String networkId;
    private String fixedIp;
    private String portId;
    private String macAddress;
    private Boolean portSecurityEnabled;
}
