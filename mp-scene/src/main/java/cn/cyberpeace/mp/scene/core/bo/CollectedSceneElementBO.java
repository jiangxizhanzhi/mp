package cn.cyberpeace.mp.scene.core.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectedSceneElementBO {
    @ApiModelProperty(value = "场景id")
    private String sceneId;
    @ApiModelProperty(value = "网元基础数据")
    private SceneElementBO sceneElement;
    @ApiModelProperty(value = "场景插件对应网元数据(字典: key-插件代号, value-插件数据[不同插件代号对应的数据模型不一样]), "
            + "cpcloud(云平台): CloudPluginSceneElementBO, "
            + "cpcollection(采集): CollectionPluginSceneElementBO, "
            + "cpflow(流量): FlowPluginSceneElementBO")
    private Map<String, Object> sceneElementPluginData;
    @ApiModelProperty(value = "场景插件对应网元端口数据(字典: key-插件代号, value-插件数据[不同插件代号对应的数据模型不一样]), "
            + "cpcloud(云平台): portId - CloudPluginSceneElementPortBO, "
            + "cpcollection(采集): portId - CollectionPluginSceneElementPortBO, "
            + "cpflow(流量): portId - FlowPluginSceneElementPortBO")
    private Map<String, Map<String, Object>> sceneElementPortPluginData;
}
