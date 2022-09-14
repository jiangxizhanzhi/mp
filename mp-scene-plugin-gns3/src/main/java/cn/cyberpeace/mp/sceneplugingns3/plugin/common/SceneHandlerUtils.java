package cn.cyberpeace.mp.sceneplugingns3.plugin.common;

import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ConnectedNetworkBO;
import cn.cyberpeace.mp.sceneplugingns3.plugin.Gns3ScenePluginHandler;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.switchs.SwitchPort;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Slf4j
public class SceneHandlerUtils {
    private Gns3ScenePluginHandler pluginHandler;

    private String switchId;

    private Map<String, SwitchPort> router2SwitchPort;
    private List<SwitchPort> switchPorts;

    public SceneHandlerUtils(Gns3ScenePluginHandler pluginHandler) {
        this.pluginHandler = pluginHandler;
        this.router2SwitchPort = new ConcurrentHashMap<>();
        this.switchPorts = new ArrayList<>();
    }

    public synchronized SwitchPort addAccessSwitchPort(Integer vlanId) {
        SwitchPort switchPort = new SwitchPort();
        switchPort.setPortNumber(switchPorts.size());
        switchPort.setName("p" + switchPort.getPortNumber());
        switchPort.setType("access");
        switchPort.setVlan(vlanId);
        switchPorts.add(switchPort);
        return switchPort;
    }

    public SwitchPort addDot1qSwitchPort() {
        SwitchPort switchPort = new SwitchPort();
        switchPort.setPortNumber(switchPorts.size());
        switchPort.setName("p" + switchPort.getPortNumber());
        switchPort.setType("dot1q");
        switchPort.setVlan(1);
        switchPorts.add(switchPort);
        return switchPort;
    }

    public void loadSwitchPorts(CloudScenePluginHandler cloudPlugin) {
        int dynamipsIdIndex = 1;
        Set<Integer> existVlanIds = new HashSet<>();
        for (var e : getPluginHandler().getElementMap().values()) {
            var cloudElement = cloudPlugin.getElement(e.getSrcElement().getId());
            List<ConnectedNetworkBO> nets = new ArrayList<>();
            nets.addAll(cloudElement.getDeriveNetworks());
            nets.addAll(cloudElement.getAccessNetworks());
            for (var net : nets) {
                if (existVlanIds.contains(net.getNetwork().getVlanId())) {
                    continue;
                }
                Integer vlanId = net.getNetwork().getVlanId();
                this.addAccessSwitchPort(vlanId);
                existVlanIds.add(vlanId);
            }
            if (cloudElement.getSrcElement().getType() == BaseSceneElementModel.Type.ROUTER) {
                SwitchPort switchPort = this.addDot1qSwitchPort();
                getRouter2SwitchPort().put(e.getSrcElement().getId(), switchPort);
                e.setDynamicId(dynamipsIdIndex++);
            }
        }
    }
}
