package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/9
 */
@Setter
@Getter
public class NetworkElementParseResult {
    private String id;
    private String name;
    private String cidr;
    private String gateway;
    private String dhcp;
    private String dhcpPool;
    private String dns;
}
