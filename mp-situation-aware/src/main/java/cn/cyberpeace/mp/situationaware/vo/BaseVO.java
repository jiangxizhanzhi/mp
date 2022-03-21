package cn.cyberpeace.mp.situationaware.vo;

import io.swagger.annotations.ApiModelProperty;
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
public class BaseVO {

    /**
     * 攻击次数
     */
    @ApiModelProperty(value = "攻击次数")
    private Long attackNumber;

    /**
     * 流量大小，单位B
     */
    @ApiModelProperty(value = "流量大小")
    private Double trafficSize;

    /**
     * 攻击次数比例
     */
    @ApiModelProperty(value = "攻击次数比例")
    private BigDecimal attackNumberPercent;

    /**
     * 攻击流量比例
     */
    @ApiModelProperty(value = "攻击流量比例")
    private BigDecimal attackTrafficPercent;


}
