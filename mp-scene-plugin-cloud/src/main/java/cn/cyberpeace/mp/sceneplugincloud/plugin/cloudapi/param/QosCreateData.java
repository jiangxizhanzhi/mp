package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

/**
 * @ClassName QosCreateData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
public class QosCreateData implements CloudHttpData {
    private String nicId;
    private Integer rate;
    private Integer delay;
    private Integer lossPercent;
    private String id;
    private String type;
}

