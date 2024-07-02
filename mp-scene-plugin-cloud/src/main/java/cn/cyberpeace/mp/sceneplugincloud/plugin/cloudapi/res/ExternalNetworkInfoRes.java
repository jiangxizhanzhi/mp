package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.res;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName ExternalNetworkInfoRes
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@Accessors(chain = true)
public class ExternalNetworkInfoRes {
    private String id;
    private String name;
    private String cidr;
    private String gatewayIp;
}
