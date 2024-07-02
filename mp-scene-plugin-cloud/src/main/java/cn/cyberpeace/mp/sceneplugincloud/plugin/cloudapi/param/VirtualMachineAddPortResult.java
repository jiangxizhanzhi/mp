package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName VirtualMachineAddPortResult
 * @Description
 * @Author maojun
 * @Date 2022/6/8
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualMachineAddPortResult implements CloudHttpData {
    private String networkId;
    private String fixedIp;
    private String portId;
    private String macAddress;
    private Boolean portSecurityEnabled;
}
