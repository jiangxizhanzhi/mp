package cn.cyberpeace.mp.scene.vo;

import cn.cyberpeace.mp.scene.domain.SceneConfigDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SceneConfigVO {
    private String name;

    public SceneConfigVO(SceneConfigDO sceneConfigDO) {
        setSceneConfig(sceneConfigDO);
    }

    public void setSceneConfig(SceneConfigDO sceneConfigDO) {
        name = sceneConfigDO.getName();
    }
}
