package cn.cyberpeace.mp.devops.cluster.converter.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtEntity{

    /**
     * ID
     */
    private Integer id;

    /**
     * 设备UUID
     */
    private String deviceUuid;

    /**
     * 实体状态
     */
    private Boolean entityStatus;

    /**
     * 型号
     */
    private String model;

    /**
     * 参数
     */
    private String params;

    /**
     * 接入类型
     */
    private String accessType;

    /**
     * 交换机端口
     */
    private String accessSwitchPort;

    /**
     * 实体端口名
     */
    private String accessSwitchPortName;

    /**
     * IP地址
     */
    private String accessIpAddress;

    /**
     * IMAC地址
     */
    private String accessMacAddress;

    /**
     * 管理协议列表
     */
    private String accessProtocols;

    /**
     * SNMP版本
     */
    private String snmpVersion;

    /**
     * SNMP配置
     */
    private String snmpConfig;

    /**
     * SNMP CPU占用率table根节点OID
     */
    private String snmpCpuOid;

    /**
     * SNMP 内存占用率table根节点OID
     */
    private String snmpMemOid;

    /**
     * SNMP 磁盘占用率table根节点OID
     */
    private String snmpDiskOid;

    /**
     * 串口端口
     */
    private String ccmPort;

    /**
     * 串口用户名
     */
    private String ccmName;

    /**
     * 串口密码
     */
    private String ccmPassword;


    private List<AccessProtocol> accessProtocolList;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class AccessProtocol{

        /**
         * 协议
         */
        private String protocol;

        /**
         * 端口
         */
        private Integer port;

        /**
         * 用户名
         */
        private String username;

        /**
         * 密码
         */
        private String password;

    }
}
