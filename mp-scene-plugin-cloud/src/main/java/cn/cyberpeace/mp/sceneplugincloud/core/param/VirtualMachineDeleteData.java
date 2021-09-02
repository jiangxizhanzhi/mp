package cn.cyberpeace.mp.sceneplugincloud.core.param;

import cn.cyberpeace.mp.sceneplugincloud.core.common.HttpData;
import lombok.Data;

import java.util.List;

@Data
public class VirtualMachineDeleteData implements HttpData {
    private List<String> vms;
    private List<String> connections;
    private List<String> portMappings;
    private String qos;
    private String floatingIp;
}
