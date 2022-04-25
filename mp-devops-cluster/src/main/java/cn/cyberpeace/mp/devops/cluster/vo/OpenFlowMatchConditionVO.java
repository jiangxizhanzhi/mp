package cn.cyberpeace.mp.devops.cluster.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流表基础信息
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
