package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.BaseCollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneConfigElementModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CollectionPluginSceneConfigElementBO
        extends BaseCollectionPluginSceneElementBO<CollectionPluginSceneConfigElementModel,
        CollectionPluginSceneConfigElementBO, SceneConfigElementBO> {

    @ApiModelProperty(value = "是否编辑过")
    private Boolean edited;

    @Override
    public CollectionPluginSceneConfigElementModel createModel() {
        return new CollectionPluginSceneConfigElementModel();
    }
}
