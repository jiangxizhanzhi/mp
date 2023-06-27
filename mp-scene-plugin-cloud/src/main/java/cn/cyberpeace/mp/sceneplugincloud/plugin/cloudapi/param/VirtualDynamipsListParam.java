package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import lombok.Data;

@Data
public class VirtualDynamipsListParam implements CloudHttpParam {
    private Integer limit;
    private Integer offset;
    private String sortKey;
    private String sortDir;
    private String ip;
}
