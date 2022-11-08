package cn.cyberpeace.mp.situationaware.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 按危险等级和目的IP维度分组
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttackSeverityAndDestinationIpAggregationBO {

   /**
    * 危险等级
    */
   private Integer severity;

   /**
    * 目的IP集合
    */
   private List<String> destinationIpList;
}
