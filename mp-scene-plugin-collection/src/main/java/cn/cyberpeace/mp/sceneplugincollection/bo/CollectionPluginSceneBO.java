package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.BaseCollectionPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneModel;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * @ClassName CollectionPluginSceneBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCollectionPluginSceneBO
 */
@Data
public class CollectionPluginSceneBO extends BaseCollectionPluginSceneBO<CollectionPluginSceneModel,
        CollectionPluginSceneBO, SceneBO> {

    @ApiModelProperty(value = "流量采集id")
    private String flowCollector;

    @ApiModelProperty(value = "流量采集器即arkime容器的id")
    private String flowCollectorContainerId;

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
        if (StringUtils.isNoneBlank(obj.getOptionKey())) {
            this.setOptionKey(JsonCommon.parse(obj.getOptionKey(), new TypeReference<List<Integer>>() {
            }));
        } else {
            this.setOptionKey(Collections.emptyList());
        }

    }

    @Override
    public CollectionPluginSceneModel toModel(String... ignoreFields) {
        CollectionPluginSceneModel obj = createModel();
        BeanUtils.copyPropertiesIgnoreNull(this, obj, ignoreFields);
        if (CollectionUtils.isEmpty(this.getOptionKey())) {
            obj.setOptionKey("");
        } else {
            obj.setOptionKey(JsonCommon.snakeStringify(this.getOptionKey()));
        }
        return obj;
    }
}
