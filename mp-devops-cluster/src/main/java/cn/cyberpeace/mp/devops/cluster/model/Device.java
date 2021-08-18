package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.model.common.ExtGateway;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtSystemInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 设备信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device extends BaseCloudPlatformModel{

    /**
     * ID
     */
    private Integer id;

    /**
     * uuid
     */
    private String uuid;

    /**
     * 更新时间
     */
    private String updatedAt;

    /**
     * 删除标记
     */
    private Boolean deleted;

    /**
     * 创建时间
     */
    private String createdAt;

    /**
     * 删除时间
     */
    private String deletedAt;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 图标地址
     */
    private String logo;

    /**
     * 类型
     */
    private String imageType;

    /**
     *
     */
    private Integer maintype;

    /**
     * 角色
     */
    private Integer role;

    /**
     * 分类
     */
    private Integer category;

    /**
     * 第二个分类
     */
    private Integer secondCategory;

    /**
     * 实体信息
     */
    private ExtEntity extEntity;


    /**
     * 系统信息
     */
    private ExtSystemInfo extSystemInfo;

    /**
     * 网关信息
     */
    private ExtGateway extGateway;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ExtEntity{

        /**
         * ID
         */
        private Integer id;

        /**
         * UUID
         */
        private String uuid;

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
        private String style;

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
        private String accessProtocals;

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
    }
}
