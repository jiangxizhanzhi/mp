package cn.cyberpeace.mp.situationaware.bo;

import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamAttackBO extends BaseBO{


    
    private String teamName;

    
    private List<AttackWayAggregationBO> attackWayList;

}
