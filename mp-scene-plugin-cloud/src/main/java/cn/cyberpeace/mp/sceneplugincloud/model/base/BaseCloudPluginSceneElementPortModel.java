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

    
    @DefaultStr
    private String entityAccessType;
    @DefaultStr
    private String entitySwitchName;
    @DefaultStr
    private String entitySwitchPort;
    @DefaultStr
    private String entityPort;
    @DefaultInt
    private Integer gatewayPortType;
    @DefaultStr
    private String gatewayPortIps;

    
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

    
    @DefaultStr
    private String firewallResourceId;

    
    @DefaultStr
    private String ospfIp;
    @DefaultStr
    private String ospfArea;

    
    @DefaultStr
    private String bgpAs;
    @DefaultStr
    private String bgpIp;
    @DefaultStr
    private String bgpPeer;
    @DefaultStr
    private String bgpPeerAs;
    @DefaultStr
    private String connectMode;

    @DefaultStr
    private String ripIp;
    @DefaultStr
    private String ripPear;

}
