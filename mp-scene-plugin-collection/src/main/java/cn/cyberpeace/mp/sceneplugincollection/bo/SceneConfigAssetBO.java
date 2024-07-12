 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneConfigAssetBO.java.
 // File size 387.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
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
