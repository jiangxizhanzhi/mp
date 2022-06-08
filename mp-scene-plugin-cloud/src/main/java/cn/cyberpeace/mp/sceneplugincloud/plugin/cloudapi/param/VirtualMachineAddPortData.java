package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

/**
 * @Description
 * @Author maojun
 * @Date 2022/6/8
 */
@Data
public class VirtualMachineAddPortData implements CloudHttpData {
    private String networkId;
    private String fixedIp;
}
