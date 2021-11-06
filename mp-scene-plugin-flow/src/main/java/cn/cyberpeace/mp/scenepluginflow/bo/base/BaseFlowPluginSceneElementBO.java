package cn.cyberpeace.mp.scenepluginflow.bo.base;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginflow.model.base.BaseFlowPluginSceneElementModel;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class BaseFlowPluginSceneElementBO<M extends BaseFlowPluginSceneElementModel,
        T extends BaseFlowPluginSceneElementBO<M, T, S>, S extends BaseSceneElementBO<?, ?, S>>
        extends BasePluginSceneElementBO<M, T, S> {

    @ApiModelProperty(value = "流量列表")
    private List<Flow> flows;

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseFlowPluginSceneElementModel::getFlows)));
        flows = StringUtils.isEmpty(obj.getFlows()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getFlows(), new TypeReference<List<Flow>>(){});
    }

    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseFlowPluginSceneElementModel::getFlows)));
        if (flows != null) {
            obj.setFlows(JsonCommon.stringify(flows));
        }
        return obj;
    }

}
