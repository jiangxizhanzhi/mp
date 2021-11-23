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
     * 攻击方式比例
     */
    private BigDecimal attackWayPercent;

    /**
     * 攻击总次数
     */
    private Long attackNumberTotal;

}
