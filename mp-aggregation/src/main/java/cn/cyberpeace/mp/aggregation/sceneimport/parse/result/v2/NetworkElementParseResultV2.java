 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NetworkElementParseResultV2.java.
 // File size 1227.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.sceneimport.parse.result.v2;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NetworkElementParseResult;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @ClassName NetworkElementParseResultV2
 * @Description
 * @Author maojun
 * @Date 2021/10/9
 * @Version 1.0.0
 */
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
