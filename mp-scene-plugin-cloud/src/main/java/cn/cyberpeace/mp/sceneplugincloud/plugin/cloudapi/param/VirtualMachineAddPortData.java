package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

/**
 * @ClassName VirtualMachineAddPortData
 * @Description
 * @Author maojun
 * @Date 2022/6/8
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
public class VirtualMachineAddPortData implements CloudHttpData {
    private String networkId;
    private String fixedIp;
    private String portId;
}
