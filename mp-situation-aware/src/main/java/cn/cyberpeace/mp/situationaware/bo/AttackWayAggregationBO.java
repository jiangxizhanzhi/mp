package cn.cyberpeace.mp.situationaware.bo;

import lombok.*;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttackWayAggregationBO extends BaseBO {

    
    private String aggregationType;
}
