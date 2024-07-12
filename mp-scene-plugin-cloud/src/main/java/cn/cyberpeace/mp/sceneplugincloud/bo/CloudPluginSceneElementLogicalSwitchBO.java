 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudPluginSceneElementLogicalSwitchBO.java.
 // File size 2904.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
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

/**
 * @ClassName CloudPluginSceneElementLogicalSwitchBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ModelBO
 */
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

    /**
     * createModel
     *
     * @return
     */
    @Override
    public CloudPluginSceneElementLogicalSwitchModel createModel() {
        return new CloudPluginSceneElementLogicalSwitchModel();
    }

    /**
     * fromModel
     *
     * @param obj
     * @param ignoreFields
     * @return
     */
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

    /**
     * toModel
     *
     * @param ignoreFields
     * @return
     */
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
