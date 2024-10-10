package cn.cyberpeace.mp.cscene.dto.scene;

import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CsceneLoadDto {
    @ApiModelProperty(value = "拓扑节点基本数据")
    private SceneConfigElementBO sceneConfigElement;
    @ApiModelProperty(value = "拓扑节点设备数据")
    private CloudPluginSceneConfigElementBO pluginSceneConfigElement;
    @ApiModelProperty(value = "启动场景id")
    private String sceneId;
    @ApiModelProperty(value = "场景实例id")
    private String csceneId;
    @ApiModelProperty(value = "场景上下文数据")
    private CsceneContextBO context;
}
