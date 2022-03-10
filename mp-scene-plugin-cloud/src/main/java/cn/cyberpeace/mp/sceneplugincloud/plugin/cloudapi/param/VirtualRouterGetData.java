package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VirtualRouterGetData implements CloudHttpData {
    private String id;
    private String name;
}
