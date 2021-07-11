package cn.cyberpeace.mp.situationaware.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttackDateHistogramNetworkBytesVO {

   
   @ApiModelProperty(value = "时间")
   private String time;

   
   @ApiModelProperty(value = "流量大小")
   private Double networkBytes;
}
