package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class NetworkModel{

    /**
     * ID
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 网络类型：flat、vlan、vxlan、gre
     */
    private String networkType;

    /**
     * mtu大小
     */
    private Integer mtu;

    /**
     * 段ID
     */
    private Integer segmentationId;

    /**
     *
     */
    private Integer portLimit;

    /**
     * 端口安全开启状态
     */
    private Boolean portSecurityEnabled;

    /**
     *
     */
    private String bridgeName;

    /**
     *
     */
    private String hostname;

    /**
     *
     */
    private String mechanismDriver;

    /**
     * 子网信息
     */
    private Subnet subnet;

    /**
     * 关联的QOS策略ID
     */
    private String qosPolicyId;

    /**
     * 状态：ACTIVE、DOWN、BUILD、ERROR
     */
    private String status;

    /**
     * 项目
     */
    private String project;

    /**
     * 创建时间
     */
    private String createdAt;
    /**
     * 更新时间
     */
    private String updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Subnet{

        private String id;

        private String name;

        /**
         * 子网CIDR
         */
        private String cidr;

        /**
         * IP版本
         */
        private Integer ipVersion;

        /**
         * 网关ip
         */
        private String gatewayIp;

        /**
         * DHCP开启状态
         */
        private Boolean enableDhcp;

        /**
         * 分配池列表
         */
        private List<AllocationPools> allocationPools;

        /**
         * DNS域名服务器列表
         */
        private List<Object> dnsNameservers;

        /**
         * 主机路由
         */
        private List<Object> hostRoutes;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static final class AllocationPools{

            private String start;

            private String end;
        }
    }
}
