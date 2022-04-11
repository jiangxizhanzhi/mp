package cn.cyberpeace.mp.situationaware.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseBO {

    /**
     * 攻击次数
     */
    private Long attackNumber;

    /**
     * 流量大小，单位B
     */
    private Double trafficSize;

    /**
     * 攻击次数比例
     */
    private BigDecimal attackNumberPercent;

    /**
     * 攻击流量比例
     */
    private BigDecimal attackTrafficPercent;

}
