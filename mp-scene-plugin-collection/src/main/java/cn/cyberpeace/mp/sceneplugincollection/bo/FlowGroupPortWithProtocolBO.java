 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowGroupPortWithProtocolBO.java.
 // File size 694.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName FlowGroupPortWithProtocolBO
 * @Description 端口维度
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
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
