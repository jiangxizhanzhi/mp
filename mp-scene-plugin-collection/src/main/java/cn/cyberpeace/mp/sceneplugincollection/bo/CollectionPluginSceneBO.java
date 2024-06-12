package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.BaseCollectionPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneModel;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CollectionPluginSceneBO extends BaseCollectionPluginSceneBO<CollectionPluginSceneModel,
        CollectionPluginSceneBO, SceneBO> {

    @ApiModelProperty(value = "流量采集id")
    private String flowCollector;

    @ApiModelProperty(value = "流量镜像id")
    private String flowMirrorId;

    @ApiModelProperty(value = "数据采集id")
    private String probeCollector;

    @ApiModelProperty(value = "采集路由id")
    private String probeRouter;

    @ApiModelProperty(value = "流量采集标识")
    private List<Integer> optionKey;

    @Override
    public CollectionPluginSceneModel createModel() {
        return new CollectionPluginSceneModel();
    }

    public void fromModel(CollectionPluginSceneModel obj, String... ignoreFields) {
        BeanUtils.copyPropertiesIgnoreNull(obj, this, ignoreFields);
        this.setOptionKey(JsonCommon.parse(obj.getOptionKey(), new TypeReference<List<Integer>>() {
        }));
    }

    @Override
    public CollectionPluginSceneModel toModel(String... ignoreFields) {
        CollectionPluginSceneModel obj = createModel();
        BeanUtils.copyPropertiesIgnoreNull(this, obj, ignoreFields);
        obj.setOptionKey(JsonCommon.snakeStringify(this.getOptionKey()));
        return obj;
    }
}
