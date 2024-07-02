package cn.cyberpeace.mp.devopscluster.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName OpenFlowMatchConditionVO
 * @Description 流表基础信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
public class OpenFlowMatchConditionVO {

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 入端口
     */
    private Integer inPort;

    /**
     *
     */
    private Integer dlVlan;


}
