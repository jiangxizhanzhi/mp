package cn.cyberpeace.mp.situationaware.bo;

import cn.cyberpeace.mp.situationaware.model.FlowClassificationFromJsonModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 聚合攻击方式
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttackClassificationBO {

   private List<Classification> classifications;

   @Data
   @NoArgsConstructor
   @AllArgsConstructor
   public static final class Classification{

      private String name;

      private String en_desc;

      private String zh_desc;

      private Integer severity;
   }

   public static AttackClassificationBO getBoFromJsonModel(FlowClassificationFromJsonModel flowClassificationFromJsonModel) {
      List<Classification> classificationsFromJsonModel = new ArrayList<>();
      for (FlowClassificationFromJsonModel.FlowType flowType : flowClassificationFromJsonModel.getClassifications()) {
         List<Classification> flowEvents = flowType.getFlow_events();
         classificationsFromJsonModel.addAll(flowEvents);
      }
      return new AttackClassificationBO(classificationsFromJsonModel);
   }
}
