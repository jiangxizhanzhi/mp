package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualMachinePrivateOperateData implements CloudHttpData {
    private String networkId;
    private String instanceIp;
    private String username;
    private String password;
    private String privateKeyPath;
    private Integer port;
}
