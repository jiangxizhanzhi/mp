package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.BaseCollectionPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneElementPortModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

@Data
public class CollectionPluginSceneElementPortBO
        extends BaseCollectionPluginSceneElementPortBO<CollectionPluginSceneElementPortModel,
        CollectionPluginSceneElementPortBO> {

    @JsonIgnore
    private SceneElementPortBO srcPort;

    @Override
    public CollectionPluginSceneElementPortModel createModel() {
        return new CollectionPluginSceneElementPortModel();
    }

    @Override
    public CollectionPluginSceneElementPortModel toModel(String... ignoreFields) {
        return super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(CollectionPluginSceneElementPortBO::getSrcPort)));
    }

}
