package cn.cyberpeace.mp.sceneplugincollection.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 端口维度
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowGroupPortWithProtocolBO {

   /**
    * 端口
    */
   private String port;

    /**
     * 协议
    */
   private String protocol;

    /**
     * 攻击次数
     */
    private Long attackNumber;

    /**
     * 流量大小，单位B
     */
    private Double trafficSize;
}
