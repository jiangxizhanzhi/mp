package cn.cyberpeace.mp.situationaware.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

}
