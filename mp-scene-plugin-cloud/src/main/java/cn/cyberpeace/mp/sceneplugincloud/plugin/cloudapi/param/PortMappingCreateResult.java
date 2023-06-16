package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
