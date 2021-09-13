package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouterModel{

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
     * 外部网关信息
     */
    private ExternalGateway externalGateway;

    /**
     * 路由器规则列表
     */
    private List<Routes> routes;

    /**
     *
     */
    private List<Interfaces> interfaces;

    /**
     * 路由器状态
     */
    private String status;

    /**
     * 项目ID
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
    public static final class ExternalGateway{

        /**
         * 外部网络ID
         */
        private String networkId;

        /**
         * 外部网关地址
         */
        private String fixedIp;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Interfaces{

        /**
         * 外部网络ID
         */
        private String networkId;

        /**
         * 外部网关地址
         */
        private String fixedIp;

        /**
         * 端口ID
         */
        private String portId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Routes{

        /**
         * 目标CIDR
         */
        private String destination;

        /**
         * 下一跳
         */
        private String nexthop;
    }
}
