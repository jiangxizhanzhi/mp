package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementPortModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
}
