package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 扩展实体信息
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtEntity implements Serializable {

    /**
     * 实体状态
     */
    @ApiModelProperty(value = "实体状态",hidden = true)
    private Boolean entityStatus;

    /**
     * 型号
     */
    @ApiModelProperty("型号")
    private String style;

    /**
     * 参数
     */
    @ApiModelProperty("参数")
    private String params;

    /**
     * SNMP版本
     */
    @ApiModelProperty("SNMP版本")
    private String snmpVersion;

    @ApiModelProperty("SNMP配置")
    private SnmpConfig snmpConfig;

    /**
     * SNMP CPU占用率table根节点OID
     */
    @ApiModelProperty("SNMP CPU占用率table根节点OID")
    private String snmpCpuOid;

    /**
     * SNMP 内存占用率table根节点OID
     */
    @ApiModelProperty("SNMP 内存占用率table根节点OID")
    private String snmpMemOid;

    /**
     * SNMP 磁盘占用率table根节点OID
     */
    @ApiModelProperty(" SNMP 磁盘占用率table根节点OID")
    private String snmpDiskOid;

    @ApiModelProperty("串口服务器名称")
    private String ccmServerName;

    /**
     * 串口端口
     */
    @ApiModelProperty("串口端口")
    private String ccmPort;

    /**
     * 串口用户名
     */
    @ApiModelProperty("串口用户名")
    private String ccmUsername;

    /**
     * 串口密码
     */
    @ApiModelProperty("串口密码")
    private String ccmPassword;
}
