package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementPortModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

@Data
public class CloudPluginSceneElementPortBO extends BaseCloudPluginSceneElementPortBO<CloudPluginSceneElementPortModel,
        CloudPluginSceneElementPortBO> {

    @JsonIgnore
    private SceneElementPortBO srcPort;

    @Override
    public CloudPluginSceneElementPortModel createModel() {
        return new CloudPluginSceneElementPortModel();
    }

    @Override
    public CloudPluginSceneElementPortModel toModel(String... ignoreFields) {
        return super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(CloudPluginSceneElementPortBO::getSrcPort)));
    }
}
