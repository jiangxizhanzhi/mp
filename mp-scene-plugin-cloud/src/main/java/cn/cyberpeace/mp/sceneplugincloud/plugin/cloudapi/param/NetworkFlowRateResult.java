package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.List;


@Data
public class NetworkFlowRateResult implements CloudHttpData {

    
    private List<Long> sumRateList;

    
    private Integer timeInt;

    
    private String unit;

}
