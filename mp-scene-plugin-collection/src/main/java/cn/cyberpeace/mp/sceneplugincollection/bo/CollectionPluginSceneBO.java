package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.BaseCollectionPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CollectionPluginSceneBO extends BaseCollectionPluginSceneBO<CollectionPluginSceneModel,
        CollectionPluginSceneBO, SceneBO> {

    @ApiModelProperty(value = "流量采集id")
    private String flowCollector;

    @ApiModelProperty(value = "数据采集id")
    private String probeCollector;

    @Override
    public CollectionPluginSceneModel createModel() {
        return new CollectionPluginSceneModel();
    }
}
