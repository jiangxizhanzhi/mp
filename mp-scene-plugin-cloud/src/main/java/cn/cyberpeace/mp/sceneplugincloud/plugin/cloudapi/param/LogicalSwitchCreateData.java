package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.List;

@Data
public class LogicalSwitchCreateData implements CloudHttpData {
    private String logicalSwitchName;
    private List<PhysicalPort> physicalPorts;
    private String physicalSwitchName;

    @Data
    public static class PhysicalPort {
        private String port;
        // 2-4094
        private Integer vlan;
    }
}