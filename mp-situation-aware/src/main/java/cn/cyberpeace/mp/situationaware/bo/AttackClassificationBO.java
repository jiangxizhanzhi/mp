package cn.cyberpeace.mp.situationaware.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
   }
}
