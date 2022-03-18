package cn.cyberpeace.mp.sceneplugincloud.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_sceneplugincloud_scene_element_network")
public class CloudPluginSceneElementNetworkModel extends BasePluginSceneElementModel {
    @DefaultStr
    private String resourceId;
    @DefaultStr
    private String cidr;
    @DefaultStr
    private String gateway;
    @DefaultStr
    private String dnsNameservers;
    @DefaultBool(true)
    private Boolean dhcp;
    @DefaultStr
    private String allocationPools;
    @DefaultStr
    private String hostRoutes;
    @DefaultBool
    private Boolean vlan;
    @DefaultInt
    private Integer vlanId;
    @DefaultBool
    private Boolean external;
    @DefaultBool
    private Boolean auxiliaryNetworkRouterOn;
    @DefaultStr
    private String auxiliaryNetworkRouterResourceId;
    @DefaultStr
    private String auxiliaryNetworkRouterIp;
}
