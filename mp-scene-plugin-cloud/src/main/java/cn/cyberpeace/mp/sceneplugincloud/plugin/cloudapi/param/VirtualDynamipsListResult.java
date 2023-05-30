package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.List;

@Data
public class VirtualDynamipsListResult implements CloudHttpData {
    private List<VirtualDynamicCreateData> items;
    private Integer total;
    private Boolean success;
}
