package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.List;

@Data
public class VirtualDynamicCreateData implements CloudHttpData {
    private String name;
    private String image;
    private String host;
    private Integer portNum;
    private List<Network> networks;

    @Data
    public static class Network {
        private String networkId;
        private String slotPort;
        private String macAddress;
    }
}
