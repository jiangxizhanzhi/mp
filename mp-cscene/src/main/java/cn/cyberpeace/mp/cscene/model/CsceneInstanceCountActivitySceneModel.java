 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneInstanceCountActivitySceneModel.java.
 // File size 537.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CsceneInstanceCountActivitySceneModel
 * @Description
 * @Author wanggang
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CsceneInstanceCountActivitySceneModel {

    /**
     * 场景总数
     */
    private Integer total;

    /**
     * 活动场景总数
     */
    private Integer activitySceneTotal;
}
