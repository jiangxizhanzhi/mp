package cn.cyberpeace.mp.aggregation.sceneimport.parse.result.v2;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NetworkElementParseResult;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


@Setter
@Getter
public class NetworkElementParseResultV2 {
    private String id;
    private String name;
    private String position;
    private String cidr;
    private String gateway;
    private String dhcp;
    private String dhcpPool;
    private List<String> dns;
    private String displayContext;

    public NetworkElementParseResult convert() {
        NetworkElementParseResult result = new NetworkElementParseResult();
        result.setId(id);
        result.setName(name);
        result.setCidr(cidr);
        result.setGateway(gateway);
        result.setDhcp(dhcp);
        result.setDhcpPool(dhcpPool);
        result.setPosition(position);
        result.setDns(StringUtils.join(dns, ","));
        result.setDisplayContext(displayContext);
        return result;
    }
}
