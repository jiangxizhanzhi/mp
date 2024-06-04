package cn.cyberpeace.mp.devopscluster.cloud.platform.result;

import lombok.Data;

import java.util.List;

@Data
public class Network {
    private String id;
    private String name;
    private String description;
    private String networkType;
    private String status;
    private String mtu;
    private String cidr;
    private String comment;
    private Subnet subnet;
    private String createAt;
    private String runningTime;

    private String sceneName;

    @Data
    public static class Subnet {
        private String name;
        private String cidr;
        private String gatewayIp;
        private List<String> dnsNameservers;
    }
}