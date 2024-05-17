package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.Data;

import java.util.List;

@Data
public class InstanceModifyNetworkResult {
    private String id;
    private String name;
    private List<Nic> nics;

    @Data
    public static class Nic {
        private String id;
        private String name;
        private String macAddress;
        private String networkName;
        private String networkInterfaceId;
        private List<String> ipv4;
        private List<String> ipv6;
    }
}

