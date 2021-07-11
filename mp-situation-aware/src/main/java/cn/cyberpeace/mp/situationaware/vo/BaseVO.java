package cn.cyberpeace.mp.situationaware.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseVO {

    
    @ApiModelProperty(value = "攻击次数")
    private Long attackNumber;

    
    @ApiModelProperty(value = "流量大小")
    private Double trafficSize;

    
    @ApiModelProperty(value = "攻击次数比例")
    private BigDecimal attackNumberPercent;

    
    @ApiModelProperty(value = "攻击流量比例")
    private BigDecimal attackTrafficPercent;


}
