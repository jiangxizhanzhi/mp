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
