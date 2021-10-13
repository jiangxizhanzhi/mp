package cn.cyberpeace.mp.cscene.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wanggang
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
