package cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api;

import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.common.Gns3Client;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.base.BaseProperties;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.base.Port;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.switchs.SwitchNodeCreateData;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.switchs.SwitchPort;

import java.util.ArrayList;
import java.util.List;


public class Switch extends Gns3Client {
    public String create(String projectId, String name, List<SwitchPort> switchPorts) {
        SwitchNodeCreateData switchNodeCreateData = new SwitchNodeCreateData();
        switchNodeCreateData.setName(name);
        List<Port> ports = new ArrayList<>();

        for (var switchPort : switchPorts) {
            Port port = new Port();
            port.setPortNumber(Integer.valueOf(switchPort.getPortNumber()));
            port.setName(switchPort.getName());
            port.setVlan(switchPort.getVlan());
            port.setType(switchPort.getType());
            ports.add(port);
        }

        BaseProperties properties = new BaseProperties();
        properties.setPortsMapping(ports);
        switchNodeCreateData.setProperties(properties);

        return super.create(projectId, switchNodeCreateData);
    }
}
