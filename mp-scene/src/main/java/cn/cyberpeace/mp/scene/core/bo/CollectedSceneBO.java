package cn.cyberpeace.mp.scene.core.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectedSceneBO {
    private SceneBO scene;
    private Map<String, Object> scenePluginData;
}
