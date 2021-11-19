package cn.cyberpeace.mp.cscene.bo;

import cn.cyberpeace.mp.base.context.SysContextHolder;
import cn.cyberpeace.mp.scene.core.bo.SceneContextBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginContext;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CsceneContextBO {

    @ApiModelProperty(value = "场景访问用户id")
    private String accessUserId;

    @ApiModelProperty(value = "透明上下文")
    private Map<String, Object> context;

    public void loadTransparentContext() {
        if (context != null) {
            SysContextHolder.setPassThroughMap(context);
        }
    }

    public SceneContextBO toSceneContext() {
        var sceneContext = new SceneContextBO();
        Map<String, Object> plugins = new HashMap<>();
        plugins.put(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY,
                new CloudScenePluginContext(accessUserId));
        sceneContext.setPlugins(plugins);
        return sceneContext;
    }
}
