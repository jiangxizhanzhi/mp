package cn.cyberpeace.mp.sceneplugincollection.bo;

import lombok.Data;

import java.util.List;

@Data
public class SceneAssetsBO {
    private String sceneId;
    private int nodeCount;
    private int ipCount;
    private List<CategoryGroupBO> categoryGroups;
}
