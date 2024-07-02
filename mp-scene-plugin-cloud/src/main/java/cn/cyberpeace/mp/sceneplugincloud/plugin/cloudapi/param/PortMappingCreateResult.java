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
