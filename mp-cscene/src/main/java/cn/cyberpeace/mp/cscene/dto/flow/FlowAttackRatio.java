package cn.cyberpeace.mp.cscene.dto.flow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author maojun
 * @Date 2022/2/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowAttackRatio {
    /**
     * 攻击类型
     */
    private String type;
    /**
     * 攻击类型占比
     */
    private Double attackTrafficPercent;
}
