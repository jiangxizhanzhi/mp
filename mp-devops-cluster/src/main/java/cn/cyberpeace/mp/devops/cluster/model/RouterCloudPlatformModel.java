package cn.cyberpeace.mp.devops.cluster.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 路由模型，对应云平台接口返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "路由模型，对应云平台接口返回信息")
public class RouterCloudPlatformModel extends BaseCloudPlatformModel implements Serializable {

    /**
     * 路由列表
     */
    private List<RouterModel> routers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class RouterModel{

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
        private ExternalGateway external_gateway;

        /**
         * 路由器规则列表
         */
        private List<Routes> routes;

        /**
         *
         */
        private List<String> interfaces;

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
        private String created_at;
        /**
         * 更新时间
         */
        private String updated_at;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static final class ExternalGateway{

            /**
             * 外部网络ID
             */
            private String network_id;

            /**
             * 外部网关地址
             */
            private String fixed_ip;
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
}
