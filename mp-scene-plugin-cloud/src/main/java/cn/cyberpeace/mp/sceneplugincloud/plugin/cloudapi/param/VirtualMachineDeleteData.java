package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.HttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualMachineDeleteData implements HttpData {
    private List<String> vms;
    private List<String> connections;
    private List<String> portMappings;
    private String qos;
    private String floatingIp;
}
