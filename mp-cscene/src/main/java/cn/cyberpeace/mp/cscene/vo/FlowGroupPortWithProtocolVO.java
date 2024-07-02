package cn.cyberpeace.mp.cscene.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName FlowGroupPortWithProtocolVO
 * @Description 端口维度
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowGroupPortWithProtocolVO {

   /**
    * 端口
    */
   @ApiModelProperty("端口")
   private String port;

    /**
     * 协议
    */
    @ApiModelProperty("协议")
   private String protocol;

    /**
     * 攻击次数
     */
    @ApiModelProperty("攻击次数")
    private Long attackNumber;

    /**
     * 流量大小，单位B
     */
    @ApiModelProperty("流量大小")
    private Double trafficSize;
}
