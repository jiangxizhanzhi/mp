package cn.cyberpeace.mp.situationaware.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttackSeverityAndDestinationIpAggregationBO {

   
   private Integer severity;

   
   private List<String> destinationIpList;
}
