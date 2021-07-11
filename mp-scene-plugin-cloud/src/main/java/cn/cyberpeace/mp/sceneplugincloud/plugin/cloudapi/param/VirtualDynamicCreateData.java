package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.List;

@Data
public class VirtualDynamicCreateData implements CloudHttpData {
    private String name;
    private String image;
    private String host;
    private Integer portSum;
    private List<Network> networks;

    
    private String platform;
    private String deviceUuid;
    private String id;
    private String instanceId;
    private Integer telnetPort;
    private String telnetHost;
    private String createdAt;
    private String status ;

    @Data
    public static class Network {
        private String networkId;
        private String slotPort;
        private String macAddress;
        private String fixedIp;
        private String portId;
        private String tapId;
    }
}
