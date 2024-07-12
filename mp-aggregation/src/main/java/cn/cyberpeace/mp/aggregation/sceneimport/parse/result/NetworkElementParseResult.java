 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NetworkElementParseResult.java.
 // File size 555.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName NetworkElementParseResult
 * @Description
 * @Author maojun
 * @Date 2021/10/9
 * @Version 1.0.0
 */
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
