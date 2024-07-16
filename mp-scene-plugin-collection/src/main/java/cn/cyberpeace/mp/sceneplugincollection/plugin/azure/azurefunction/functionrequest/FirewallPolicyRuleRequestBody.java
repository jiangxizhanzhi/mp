package cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction.functionrequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirewallPolicyRuleRequestBody implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 操作
     */
    private String action;

    /**
     * 源IP地址
     */
    private String sourceIpAddress;

    /**
     * 协议
     */
    private String protocol;

    /**
     * 目的IP地址
     */
    private String destinationIpAddress;

    /**
     * 目的端口
     */
    private String destinationPort;
}