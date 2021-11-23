package cn.cyberpeace.mp.situationaware.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聚合攻击方式
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttackDateHistogramNetworkBytesBO {

   /**
    * 自 1970-01-01T00:00:00Z 纪元以来的毫秒数
    */
   private String time;

   /**
    * 流量大小,单位：B
    */
   private Double networkBytes;
}
