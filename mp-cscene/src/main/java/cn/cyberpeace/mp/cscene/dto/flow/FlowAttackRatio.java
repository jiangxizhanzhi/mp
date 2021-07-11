package cn.cyberpeace.mp.cscene.dto.flow;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowAttackRatio {
    
    @ApiModelProperty(value = "攻击类型")
    private String type;
    
    @ApiModelProperty(value = "攻击类型占比")
    private Double attackTrafficPercent;
}
