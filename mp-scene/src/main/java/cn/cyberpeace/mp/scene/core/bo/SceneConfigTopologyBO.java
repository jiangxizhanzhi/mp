package cn.cyberpeace.mp.scene.core.bo;

import lombok.Data;

import java.util.List;

@Data
public class SceneConfigTopologyBO {
    private SceneConfigBO scene;
    private List<SceneConfigAreaBO> areas;
    private List<SceneConfigElementBO> elements;
    private List<SceneConfigElementPortConnectionBO> connections;
}
