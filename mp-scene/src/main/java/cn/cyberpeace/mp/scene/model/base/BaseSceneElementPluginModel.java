package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import lombok.Data;

/**
 * @ClassName BaseSceneElementPluginModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
public class BaseSceneElementPluginModel extends BaseModel {
    private String scene;
    private String sceneElement;
    private String pluginKey;
}
