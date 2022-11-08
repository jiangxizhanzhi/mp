package cn.cyberpeace.mp.cscene.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CsceneInstanceCountActivitySceneBO {

    /**
     * 场景总数
     */
    private Integer total;

    /**
     * 活动场景总数
     */
    private Integer activitySceneTotal;
}
