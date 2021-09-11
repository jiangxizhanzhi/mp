package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

@Data
public class CloudPluginSceneElementBO extends BaseCloudPluginSceneElementBO<CloudPluginSceneElementModel,
        CloudPluginSceneElementBO> {

    @JsonIgnore
    private SceneElementBO srcElement;

    @Override
    public CloudPluginSceneElementModel createModel() {
        return new CloudPluginSceneElementModel();
    }

    @Override
    public CloudPluginSceneElementModel toModel(String... ignoreFields) {
        return super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(CloudPluginSceneElementBO::getSrcElement)));
    }
}
