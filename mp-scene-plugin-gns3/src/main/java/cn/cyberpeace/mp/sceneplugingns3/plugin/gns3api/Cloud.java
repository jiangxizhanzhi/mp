package cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api;

import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.common.Gns3Client;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.base.BaseProperties;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.base.Port;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.cloud.CloudNodeCreateData;

public class Cloud extends Gns3Client {

    private static String TAP = "tap";

    public String create(String projectId, String name) {
        CloudNodeCreateData cloudNodeCreateData = new CloudNodeCreateData();
        cloudNodeCreateData.setName("cloud" + name);

        Port port = new Port();
        port.setPortNumber(0);
        port.setType(TAP);
        port.setInterface(name);
        port.setName(name);

        BaseProperties properties = new BaseProperties();
        properties.addPort(port);
        cloudNodeCreateData.setProperties(properties);

        return super.create(projectId, cloudNodeCreateData);
    }
}
