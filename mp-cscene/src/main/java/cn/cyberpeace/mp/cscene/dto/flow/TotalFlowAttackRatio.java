package cn.cyberpeace.mp.cscene.dto.flow;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalFlowAttackRatio {

    
    @ApiModelProperty(value = "攻击次数")
    private Long totalAttackNumber;

    
    @ApiModelProperty(value = "流量大小，单位B")
    private Double totalTrafficSize;

    
    @ApiModelProperty(value = "攻击手法列表")
    private List<FlowAttackRatio> attackWayList;
}