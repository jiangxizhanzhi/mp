package cn.cyberpeace.mp.sceneplugincollection.bo.base;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneBO;
import cn.cyberpeace.mp.sceneplugincollection.model.base.BaseCollectionPluginSceneModel;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Set;

@Data
public abstract class BaseCollectionPluginSceneBO<M extends BaseCollectionPluginSceneModel,
        T extends BaseCollectionPluginSceneBO<M, T, S>, S extends BaseSceneBO<?, S>>
        extends BasePluginSceneBO<M, T, S> {
    @ApiModelProperty(value = "是否开启流量采集")
    private Boolean enableFlowCollect;
    @ApiModelProperty(value = "是否开启探针采集")
    private Boolean enableProbeCollect;
    @ApiModelProperty(value = "是否开启流量镜像")
    private Boolean enableFlowMirror;
    @ApiModelProperty(value = "用户选择的开启流量采集的节点")
    private Set<String> flowCollectNodes;


    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj,
                ArrayUtils.addAll(ignoreFields, BeanUtils.getBeanGetterFieldName(BaseCollectionPluginSceneModel::getFlowCollectNodes))
        );
        if (StringUtils.isNotBlank(obj.getFlowCollectNodes())) {
            this.flowCollectNodes = JsonCommon.parse(obj.getFlowCollectNodes(), new TypeReference<Set<String>>() {});
        }
    }

    @Override
    public M toModel(String... ignoreFields) {
        M obj = super.toModel(
                ArrayUtils.addAll(ignoreFields, BeanUtils.getBeanGetterFieldName(BaseCollectionPluginSceneModel::getFlowCollectNodes)
                ));
        if (Objects.isNull(this.flowCollectNodes)) {
            obj.setFlowCollectNodes("");
        } else {
            obj.setFlowCollectNodes(JsonCommon.stringify(this.flowCollectNodes));
        }
        return obj;
    }
}
