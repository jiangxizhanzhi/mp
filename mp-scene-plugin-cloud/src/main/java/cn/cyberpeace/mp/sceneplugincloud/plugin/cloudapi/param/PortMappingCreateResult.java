 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name PortMappingCreateResult.java.
 // File size 682.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName PortMappingCreateResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortMappingCreateResult {
    private String networkId;
    private String instanceIp;
    private Integer instancePort;
    private String proto;
    private String networkNamespace;
    private String id;
    private String host;
    private Integer hostPort ;
    private Boolean success;
    private String requestId;
}
