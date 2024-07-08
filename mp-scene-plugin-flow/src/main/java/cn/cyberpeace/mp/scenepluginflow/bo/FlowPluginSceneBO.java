package cn.cyberpeace.mp.scenepluginflow.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scenepluginflow.bo.base.BaseFlowPluginSceneBO;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 流量插件场景业务对象。
 * 继承自BaseFlowPluginSceneBO，用于处理流量插件场景的业务逻辑。
 * 该类封装了与流量插件场景相关的数据和操作，包括创建模型等。
 *
 * @param <FlowPluginSceneModel> 流量插件场景模型类，用于数据持久化或传输。
 * @param <FlowPluginSceneBO>    流量插件场景业务对象类，用于业务逻辑处理。
 * @param <SceneBO>              场景业务对象类，用于通用场景业务逻辑处理。
 */
@Data
public class FlowPluginSceneBO extends BaseFlowPluginSceneBO<FlowPluginSceneModel, FlowPluginSceneBO, SceneBO> {

    /**
     * 流量发生器ID，用于标识流量发生器的唯一性。
     */
    @ApiModelProperty(value = "流量发生器id")
    private String executor;

    /**
     * 创建流量插件场景模型实例。
     * 该方法用于根据业务对象创建对应的模型对象，以便进行数据持久化或传输。
     *
     * @return 流量插件场景模型的新实例。
     */
    @Override
    public FlowPluginSceneModel createModel() {
        return new FlowPluginSceneModel();
    }
}

