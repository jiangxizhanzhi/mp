package cn.cyberpeace.mp.scene.core.vo;

import lombok.Data;

import java.util.List;

@Data
public class SceneConfigTopologyVO {
    private SceneConfigVO scene;
    private List<SceneConfigAreaVO> areas;
    private List<SceneConfigElementVO> elements;
    private List<SceneConfigElementPortConnectionVO> connections;
}
