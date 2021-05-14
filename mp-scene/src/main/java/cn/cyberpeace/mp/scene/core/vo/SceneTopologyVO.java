package cn.cyberpeace.mp.scene.core.vo;

import lombok.Data;

import java.util.List;

@Data
public class SceneTopologyVO {
    private SceneVO scene;
    private List<SceneAreaVO> areas;
    private List<SceneElementVO> elements;
    private List<SceneElementPortConnectionVO> connections;
}
