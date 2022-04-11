package cn.cyberpeace.mp.situationaware.vo;

import cn.cyberpeace.mp.situationaware.bo.AttackWayAggregationBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamAttackVO extends BaseVO{

    /**
     * 战队名称
     */
    @ApiModelProperty("战队名称")
    private String teamName;

    /**
     * 攻击手法列表
     */
    @ApiModelProperty("攻击手法占比top5")
    private List<AttackWayAggregationBO> attackWayList;
}
