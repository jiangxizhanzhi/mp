package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MibNodeQueryParam implements CloudHttpParam {

    private String oid;
    
    private Integer version;
    
    private String communityName;
    
    private String ip;
    
    private Integer port;
    
    private String username;
    private String authKey;
    private String privKey;

}
