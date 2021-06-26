package cn.cyberpeace.mp.devops.cluster.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 网络代理模型，对应云平台接口返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "网络代理模型，对应云平台接口返回信息")
public class NetworkAgentOpenStackModel extends BaseOpenStackModel implements Serializable {

    /**
     * 网络代理
     */
    private List<NetworkAgentModel> agents;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class NetworkAgentModel{

        /**
         * ID
         */
        private String id;

        /**
         * agent_type
         */
        private String agent_type;

        /**
         * binary
         */
        private String binary;

        /**
         * topic
         */
        private String topic;

        /**
         * host
         */
        private String host;

        /**
         * admin_state_up
         */
        private Boolean admin_state_up;

        /**
         *
         */
        private String created_at;

        /**
         *
         */
        private String started_at;

        /**
         *
         */
        private String heartbeat_timestamp;

        /**
         * 描述
         */
        private String description;

        /**
         * resources_synced
         */
        private Object resources_synced;

        /**
         *
         */
        private String availability_zone;

        /**
         *
         */
        private Boolean alive;

        private Object configurations;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static final class Configurations{

            private String dhcp_driver;

            private Long dhcp_lease_duration;

            private Boolean log_agent_heartbeats;

            private Integer networks;

            private Integer ports;

            private Integer subnets;
        }
    }
}
