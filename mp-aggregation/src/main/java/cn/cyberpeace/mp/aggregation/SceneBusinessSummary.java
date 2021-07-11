package cn.cyberpeace.mp.aggregation;

import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
@ApiModel("用户资源")
public class SceneBusinessSummary {

    @ApiModelProperty("容器数量")
    private Integer containerCount;

    @ApiModelProperty("实装设备数量")
    private Integer entityCount;

    @ApiModelProperty("虚拟机数量")
    private Integer vmCount;

    @ApiModelProperty("NPC")
    private Integer npcCount;

    @ApiModelProperty("路由器数量")
    private Integer routerCount;

    @ApiModelProperty("交换机数量")
    private Integer switchCount;

    @ApiModelProperty("防火墙数量")
    private Integer firewallCount;

    public SceneBusinessSummary() {
        this.containerCount = 0;
        this.entityCount = 0;
        this.vmCount = 0;
        this.npcCount = 0;
        this.routerCount = 0;
        this.switchCount = 0;
        this.firewallCount = 0;
    }


    public static List<Integer> switchType() {
        List<Integer> roleType = new ArrayList<>();
        roleType.add(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY);
        roleType.add(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH);
        return roleType;
    }

    public static List<Integer> routerType() {
        List<Integer> roleType = new ArrayList<>();
        roleType.add(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER);
        return roleType;
    }

    public static List<Integer> npcType() {
        List<Integer> roleType = new ArrayList<>();
        roleType.add(BaseCloudPluginSceneElementModel.Role.NPC);
        return roleType;
    }

    public static List<Integer> firewallType() {
        List<Integer> roleType = new ArrayList<>();
        roleType.add(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_FIREWALL);
        return roleType;
    }
}
