package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementPortModel;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class BaseCloudPluginSceneElementPortBO<M extends BaseCloudPluginSceneElementPortModel,
        T extends BaseCloudPluginSceneElementPortBO<M, T, S>, S extends BaseSceneElementPortBO<?, S, ?>>
        extends BasePluginSceneElementPortBO<M, T, S> {
    @ApiModelProperty(value = "云平台虚拟化资源id")
    private String resourceId;
    @ApiModelProperty(value = "是否nat外网")
    private Boolean nat;
    @ApiModelProperty(value = "虚拟防火墙规则")
    private List<FirewallRule> firewallRules;

    // 靶标属性
    @ApiModelProperty(value = "实体设备端口接入类型")
    private String entityAccessType;
    @ApiModelProperty(value = "实体设备端口链接的交换机名称")
    private String entitySwitchName;
    @ApiModelProperty(value = "实体设备端口链接的交换机端口")
    private String entitySwitchPort;
    @ApiModelProperty(value = "网关设备端口类型")
    private Integer gatewayPortType;
    @ApiModelProperty(value = "网关设备端口地址段")
    private String gatewayPortIps;

    @ApiModelProperty(value = "辅助网络id")
    private String auxiliaryNetworkId;

    @ApiModelProperty(value = "qos id")
    private String qosId;
    @ApiModelProperty(value = "接入方式")
    private List<CloudAccessMode> accessModes;
    @ApiModelProperty(value = "弹性ip")
    private String floatingIp;
    @ApiModelProperty(value = "弹性ip的id")
    private String floatingIpId;

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementPortModel::getFirewallRules),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementPortModel::getAccessModes)));
        firewallRules = StringUtils.isEmpty(obj.getFirewallRules()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getFirewallRules(), new TypeReference<List<FirewallRule>>(){});
        accessModes = StringUtils.isEmpty(obj.getAccessModes()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getAccessModes(), new TypeReference<List<CloudAccessMode>>(){});
    }

    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementPortModel::getFirewallRules),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementPortModel::getAccessModes)));
        if (firewallRules != null) {
            obj.setFirewallRules(JsonCommon.stringify(firewallRules));
        }
        if (accessModes != null) {
            obj.setAccessModes(JsonCommon.stringify(accessModes));
        }
        return obj;
    }
}
