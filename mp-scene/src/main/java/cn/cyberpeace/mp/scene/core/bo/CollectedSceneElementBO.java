package cn.cyberpeace.mp.scene.core.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectedSceneElementBO {
    private SceneElementBO sceneElement;
    private Map<String, Object> sceneElementPluginData;
    private Map<String, Map<String, Object>> sceneElementPortPluginData;
}
