package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.Data;

@Data
public class VirtualRouterNic {
    private String id;
    private String fixedIp;
    private String cidr;

    private String nicMac;
}