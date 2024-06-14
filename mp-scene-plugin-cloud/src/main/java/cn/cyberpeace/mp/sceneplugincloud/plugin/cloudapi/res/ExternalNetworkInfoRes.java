package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.res;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ExternalNetworkInfoRes {
    private String id;
    private String name;
    private String cidr;
    private String gatewayIp;
}
