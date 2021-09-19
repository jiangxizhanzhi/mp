package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.JsonUtils;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementPortBO;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementPortModel;
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
        T extends BaseCloudPluginSceneElementPortBO<M, T>> extends BasePluginSceneElementPortBO<M, T> {
    @ApiModelProperty(value = "云平台虚拟化资源id")
    private String resourceId;
    @ApiModelProperty(value = "是否nat外网")
    private Boolean nat;

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

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementPortModel::getAccessModes)));
        accessModes = StringUtils.isEmpty(obj.getAccessModes()) ? new ArrayList<>()
                : JsonUtils.parse(obj.getAccessModes(), new TypeReference<List<CloudAccessMode>>(){});
    }

    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementPortModel::getAccessModes)));
        if (accessModes != null) {
            obj.setAccessModes(JsonUtils.toJsonString(accessModes));
        }
        return obj;
    }
}
