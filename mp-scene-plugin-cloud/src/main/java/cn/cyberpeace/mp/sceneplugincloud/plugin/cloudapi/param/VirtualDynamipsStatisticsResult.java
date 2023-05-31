package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.Map;

@Data
public class VirtualDynamipsStatisticsResult implements CloudHttpData {
    private Map<String, Integer> statics;
    private Boolean success;
}
