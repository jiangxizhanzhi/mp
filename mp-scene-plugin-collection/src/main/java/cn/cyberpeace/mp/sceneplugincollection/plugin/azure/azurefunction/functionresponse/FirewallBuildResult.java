package cn.cyberpeace.mp.sceneplugincollection.plugin.azure.azurefunction.functionresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 防火墙创建结果
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FirewallBuildResult {

    //防火墙子网地址空间
    private String firewallSubnetAddressSpace;

    //防火墙子网名称
    private String firewallSubnetName;

    //防火墙静态ip
    private String firewallStaticIp;

    //创建结果
    private Boolean result;

    //错误信息
    private String errorMessage;
}
