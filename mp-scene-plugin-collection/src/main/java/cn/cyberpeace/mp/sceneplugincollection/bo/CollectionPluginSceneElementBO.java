package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.BaseCollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneElementModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

@Data
public class CollectionPluginSceneElementBO
        extends BaseCollectionPluginSceneElementBO<CollectionPluginSceneElementModel, CollectionPluginSceneElementBO> {

    @JsonIgnore
    private SceneElementBO srcElement;

    @Override
    public CollectionPluginSceneElementModel createModel() {
        return new CollectionPluginSceneElementModel();
    }

    @Override
    public CollectionPluginSceneElementModel toModel(String... ignoreFields) {
        return super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(CollectionPluginSceneElementBO::getSrcElement)));
    }
}
