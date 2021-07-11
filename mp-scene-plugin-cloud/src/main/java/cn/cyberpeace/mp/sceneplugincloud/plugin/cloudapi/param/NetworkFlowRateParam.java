package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import lombok.Data;

import java.util.List;


@Data
public class NetworkFlowRateParam implements CloudHttpParam {

    
    private List<String> ports;

    
    private Long time;
}
