package cn.cyberpeace.mp.sceneplugincollection.bo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName SceneAssetsBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class SceneAssetsBO {
    private String sceneId;
    private int nodeCount;
    private int ipCount;
    private List<CategoryGroupBO> categoryGroups;
}
