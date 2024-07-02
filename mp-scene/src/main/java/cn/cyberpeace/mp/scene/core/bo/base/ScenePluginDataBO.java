package cn.cyberpeace.mp.scene.core.bo.base;

import lombok.Data;

import java.util.Map;

/**
 * @ClassName ScenePluginDataBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseScenePluginDataBO
 */
@Data
public class ScenePluginDataBO extends BaseScenePluginDataBO<Map<String, Object>, Map<String, Object>,
        Map<String, Object>> {

    public ScenePluginDataBO export() {
        return this;
    }
}
