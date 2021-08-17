package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.model.common.ExtSystemInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备信息
 */
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
    private String updated_at;

    /**
     * 删除标记
     */
    private Boolean deleted;

    /**
     * 创建时间
     */
    private String created_at;

    /**
     * 删除时间
     */
    private String deleted_at;

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
    private String image_type;

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
    private Integer second_category;

    /**
     * 实体信息
     */
    private ExtEntity ext_entity;


    /**
     * 系统信息
     */
    private ExtSystemInfo ext_system_info;

    /**
     * 网关信息
     */
    private ExtGateway ext_gateway;

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
        private String device_uuid;

        /**
         * 实体状态
         */
        private Boolean entity_status;

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
        private String access_type;

        /**
         * 交换机端口
         */
        private String access_switch_port;

        /**
         * 实体端口名
         */
        private String access_switch_port_name;

        /**
         * IP地址
         */
        private String access_ip_address;

        /**
         * IMAC地址
         */
        private String access_mac_address;

        /**
         * 管理协议列表
         */
        private String access_protocals;

        /**
         * SNMP版本
         */
        private String snmp_version;

        /**
         * SNMP配置
         */
        private String snmp_config;

        /**
         * SNMP CPU占用率table根节点OID
         */
        private String snmp_cpu_oid;

        /**
         * SNMP 内存占用率table根节点OID
         */
        private String snmp_mem_oid;

        /**
         * SNMP 磁盘占用率table根节点OID
         */
        private String snmp_disk_oid;

        /**
         * 串口端口
         */
        private String ccm_port;

        /**
         * 串口用户名
         */
        private String ccm_name;

        /**
         * 串口密码
         */
        private String ccm_password;
    }

    /**
     * 扩展网关信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ExtGateway{

        private Integer id;

        private String uuid;

        private String created_at;

        private String updated_at;

        private Boolean deleted;

        private String deleted_at;

        private String user;

        private String project;

        private String device_uuid;

        private Integer port_configs;

        private String port_name;

        private String port_ips;
    }
}
