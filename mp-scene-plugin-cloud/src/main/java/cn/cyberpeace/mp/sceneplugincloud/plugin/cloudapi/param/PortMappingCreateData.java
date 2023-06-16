package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortMappingCreateData implements CloudHttpData {
    private String networkId;
    private String instanceIp;
    private Integer instancePort;
    private String proto = "tcp";
    private String host;
    private String fwdnsInterfaceIp;
}
