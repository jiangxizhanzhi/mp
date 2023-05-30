package cn.cyberpeace.mp.sceneplugincollection.bo;

import lombok.Data;

import java.util.List;

@Data
public class SceneConfigAssetBO {
    private String sceneId;
    private int nodeCount;
    private List<SceneConfigCategoryGroupBO> categoryGroups;
}
