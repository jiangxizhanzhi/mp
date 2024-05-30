package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementLogicalSwitchModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.LogicalSwitchCreateData;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CloudPluginSceneElementLogicalSwitchBO extends ModelBO<CloudPluginSceneElementLogicalSwitchModel, CloudPluginSceneElementLogicalSwitchBO> {
    @ApiModelProperty(value = "所属场景id")
    private String scene;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "物理端口集合")
    private List<LogicalSwitchCreateData.PhysicalPort> physicalPorts;
    @ApiModelProperty(value = "物理交换机名称")
    private String physicalSwitchName;

    @Override
    public CloudPluginSceneElementLogicalSwitchModel createModel() {
        return new CloudPluginSceneElementLogicalSwitchModel();
    }

    @Override
    public CloudPluginSceneElementLogicalSwitchBO fromModel(CloudPluginSceneElementLogicalSwitchModel obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(CloudPluginSceneElementLogicalSwitchModel::getPhysicalPorts))
        );
        physicalPorts = StringUtils.isEmpty(obj.getPhysicalPorts()) ?
                new ArrayList<>() : JsonCommon.parse(obj.getPhysicalPorts(), new TypeReference<List<LogicalSwitchCreateData.PhysicalPort>>() {
        });
        return this;
    }

    @Override
    public CloudPluginSceneElementLogicalSwitchModel toModel(String... ignoreFields) {
        var obj = super.toModel(
                ArrayUtils.addAll(ignoreFields,
                        BeanUtils.getBeanGetterFieldName(CloudPluginSceneElementLogicalSwitchModel::getPhysicalPorts))
        );
        if (physicalPorts != null) {
            obj.setPhysicalPorts(JsonCommon.stringify(physicalPorts));
        }
        return obj;
    }
}
