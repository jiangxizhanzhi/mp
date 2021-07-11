package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class NetworkElementParseResult {
    private String id;
    private String name;
    private String position;
    private String cidr;
    private String gateway;
    private String dhcp;
    private String dhcpPool;
    private String dns;
    private String displayContext;
}
