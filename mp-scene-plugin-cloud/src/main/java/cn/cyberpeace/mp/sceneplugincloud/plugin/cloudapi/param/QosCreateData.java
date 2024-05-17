package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

@Data
public class QosCreateData implements CloudHttpData {
    private String nicId;
    private Integer rate;
    private Integer delay;
    private Integer lossPercent;
    private String id;
    private String type;
}

