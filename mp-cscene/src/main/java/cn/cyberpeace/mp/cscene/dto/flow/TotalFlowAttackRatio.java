package cn.cyberpeace.mp.cscene.dto.flow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/2/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalFlowAttackRatio {

    /**
     * 攻击次数
     */
    private Long totalAttackNumber;

    /**
     * 流量大小，单位B
     */
    private Double totalTrafficSize;

    /**
     * 攻击手法列表
     */
    private List<FlowAttackRatio> attackWayList;
}