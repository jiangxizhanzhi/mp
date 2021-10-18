package cn.cyberpeace.mp.scene.core.bo.base;

import lombok.Data;

import java.util.Map;

@Data
public class ScenePluginDataBO extends BaseScenePluginDataBO<Map<String, Object>, Map<String, Object>,
        Map<String, Object>> {

    public ScenePluginDataBO export() {
        return this;
    }
}
