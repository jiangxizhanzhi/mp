package cn.cyberpeace.mp.sceneplugincollection.bo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName SceneConfigAssetBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class SceneConfigAssetBO {
    private String sceneId;
    private int nodeCount;
    private List<SceneConfigCategoryGroupBO> categoryGroups;
}
