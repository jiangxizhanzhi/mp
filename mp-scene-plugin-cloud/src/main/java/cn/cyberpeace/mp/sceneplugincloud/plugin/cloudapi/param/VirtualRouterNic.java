package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.Data;

/**
 * @ClassName VirtualRouterNic
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class VirtualRouterNic {
    private String id;
    private String fixedIp;
    private String cidr;

    private String nicMac;
}