package cn.cyberpeace.mp.devops.cluster.converter.model.common;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("扩展实体信息")
public class ExtEntity{

    /**
     * ID
     */
    @ApiModelProperty(value = "ID",hidden = true)
    private Integer id;

    /**
     * 设备UUID
     */
    @ApiModelProperty("设备UUID")
    private String deviceUuid;

    /**
     * 实体状态
     */
    @ApiModelProperty("实体状态")
    private Boolean entityStatus;

    /**
     * 型号
     */
    @ApiModelProperty("型号")
    private String model;

    /**
     * 参数
     */
    @ApiModelProperty("参数")
    private String params;

    /**
     * 接入类型
     */
    @ApiModelProperty("接入类型")
    private String accessType;

    /**
     * 交换机端口
     */
    @ApiModelProperty("接入类型")
    private String accessSwitchPort;

    /**
     * 实体端口名
     */
    @ApiModelProperty("实体端口名")
    private String accessSwitchPortName;

    /**
     * IP地址
     */
    @ApiModelProperty("IP地址")
    private String accessIpAddress;

    /**
     * IMAC地址
     */
    @ApiModelProperty("IP地址")
    private String accessMacAddress;

    /**
     * 管理协议列表
     */
    @ApiModelProperty(value = "管理协议列表",hidden = true)
    private String accessProtocols;

    /**
     * SNMP版本
     */
    @ApiModelProperty("SNMP版本")
    private String snmpVersion;

    /**
     * SNMP配置
     */
    @ApiModelProperty("SNMP配置")
    private String snmpConfig;

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
    @ApiModelProperty("SNMP 磁盘占用率table根节点OID")
    private String snmpDiskOid;

    /**
     * 串口端口
     */
    @ApiModelProperty("串口端口")
    private String ccmPort;

    /**
     * 串口用户名
     */
    @ApiModelProperty("串口用户名")
    private String ccmName;

    /**
     * 串口密码
     */
    @ApiModelProperty("串口密码")
    private String ccmPassword;

    /**
     * 接入方式集合
     */
    @ApiModelProperty("接入方式集合")
    private List<AccessProtocol> accessProtocolList;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccessProtocol{

        /**
         * 协议
         */
        @ApiModelProperty("协议")
        private String protocol;

        /**
         * 端口
         */
        @ApiModelProperty("端口")
        private Integer port;

        /**
         * 用户名
         */
        @ApiModelProperty("用户名")
        private String username;

        /**
         * 密码
         */
        @ApiModelProperty("密码")
        private String password;

    }
}
