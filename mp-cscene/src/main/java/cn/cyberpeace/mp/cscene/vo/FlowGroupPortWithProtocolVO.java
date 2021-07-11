package cn.cyberpeace.mp.cscene.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowGroupPortWithProtocolVO {

   
   @ApiModelProperty("端口")
   private String port;

    
    @ApiModelProperty("协议")
   private String protocol;

    
    @ApiModelProperty("攻击次数")
    private Long attackNumber;

    
    @ApiModelProperty("流量大小")
    private Double trafficSize;
}
