package cn.cyberpeace.mp.sceneplugincloud.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementPortModel;
import lombok.Data;

@Data
public class BaseCloudPluginSceneElementPortModel extends BasePluginSceneElementPortModel {

    @DefaultStr
    private String resourceId;

    @DefaultBool
    private Boolean nat;
    @DefaultStr
    private String firewallRules;

    // 靶标属性
    @DefaultStr
    private String entityAccessType;
    @DefaultStr
    private String entitySwitchName;
    @DefaultStr
    private String entitySwitchPort;
    @DefaultInt
    private Integer gatewayPortType;
    @DefaultStr
    private String gatewayPortIps;

    // 辅助信息
    @DefaultStr
    private String auxiliaryNetworkId;

    @DefaultStr
    private String qosId;
    @DefaultStr
    private String accessModes;

    @DefaultStr
    private String floatingIp;
    @DefaultStr
    private String floatingIpId;

    // 三层防火墙对应防火墙资源id
    @DefaultStr
    private String firewallResourceId;

    // ospf 组网
    @DefaultStr
    private String ospfIp;
    @DefaultStr
    private String ospfArea;

    // bgp 组网
    @DefaultStr
    private String bgpAs;
    @DefaultStr
    private String bgpIp;
    @DefaultStr
    private String bgpPeer;
    @DefaultStr
    private String bgpPeerAs;

}
