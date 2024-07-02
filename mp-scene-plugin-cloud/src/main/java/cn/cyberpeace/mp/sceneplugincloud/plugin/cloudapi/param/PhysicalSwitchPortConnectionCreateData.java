package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

/**
 * @ClassName PhysicalSwitchPortConnectionCreateData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
public class PhysicalSwitchPortConnectionCreateData implements CloudHttpData {
    private String srcSwitchName;
    private String srcPort;
    private String dstSwitchName;
    private String dstPort;
}