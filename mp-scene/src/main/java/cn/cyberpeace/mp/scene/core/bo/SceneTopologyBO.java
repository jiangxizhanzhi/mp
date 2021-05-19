package cn.cyberpeace.mp.scene.core.bo;

import lombok.Data;

import java.util.List;

@Data
public class SceneTopologyBO {
    private SceneBO scene;
    private List<SceneAreaBO> areas;
    private List<SceneElementBO> elements;
    private List<SceneElementPortConnectionBO> connections;
}
