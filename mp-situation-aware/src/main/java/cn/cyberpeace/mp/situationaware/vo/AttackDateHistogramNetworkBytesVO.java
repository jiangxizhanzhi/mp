 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AttackDateHistogramNetworkBytesVO.java.
 // File size 645.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统计攻击流量
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttackDateHistogramNetworkBytesVO {

   /**
    * 自 1970-01-01T00:00:00Z 纪元以来的毫秒数
    */
   @ApiModelProperty(value = "时间")
   private String time;

   /**
    * 流量大小,单位：B
    */
   @ApiModelProperty(value = "流量大小")
   private Double networkBytes;
}
