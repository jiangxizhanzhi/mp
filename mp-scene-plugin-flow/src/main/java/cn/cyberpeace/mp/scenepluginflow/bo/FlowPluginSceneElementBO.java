package cn.cyberpeace.mp.scenepluginflow.bo;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scenepluginflow.bo.base.BaseFlowPluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneElementModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

@Data
public class FlowPluginSceneElementBO extends BaseFlowPluginSceneElementBO<FlowPluginSceneElementModel,
        FlowPluginSceneElementBO> {

    @JsonIgnore
    private SceneElementBO srcElement;

    @Override
    public FlowPluginSceneElementModel createModel() {
        return new FlowPluginSceneElementModel();
    }

    @Override
    public FlowPluginSceneElementModel toModel(String... ignoreFields) {
        return super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(FlowPluginSceneElementBO::getSrcElement)));
    }

}
