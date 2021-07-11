package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtEntity implements Serializable {

    
    @ApiModelProperty(value = "实体状态",hidden = true)
    private Boolean entityStatus;

    
    @ApiModelProperty("型号")
    private String style;

    
    @ApiModelProperty("参数")
    private String params;

    
    @ApiModelProperty("SNMP版本")
    private String snmpVersion;

    @ApiModelProperty("SNMP配置")
    private SnmpConfig snmpConfig;

    
    @ApiModelProperty("SNMP CPU占用率table根节点OID")
    private String snmpCpuOid;

    
    @ApiModelProperty("SNMP 内存占用率table根节点OID")
    private String snmpMemOid;

    
    @ApiModelProperty(" SNMP 磁盘占用率table根节点OID")
    private String snmpDiskOid;

    @ApiModelProperty("串口服务器名称")
    private String ccmServerName;

    
    @ApiModelProperty("串口端口")
    private String ccmPort;

    
    @ApiModelProperty("串口用户名")
    private String ccmUsername;

    
    @ApiModelProperty("串口密码")
    private String ccmPassword;
}
