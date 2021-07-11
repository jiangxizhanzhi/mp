package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.List;


@Data
public class NetworkFlowRateData implements CloudHttpData {

    
    private List<String> ports;

    
    private Long time;
}
