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
public class NetworkAgentCloudPlatformModel extends BaseCloudPlatformModel implements Serializable {

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
        private String agentType;

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
        private Boolean adminStateUp;

        /**
         *
         */
        private String createdAt;

        /**
         *
         */
        private String startedAt;

        /**
         *
         */
        private String heartbeatTimestamp;

        /**
         * 描述
         */
        private String description;

        /**
         * resources_synced
         */
        private Object resourcesSynced;

        /**
         *
         */
        private String availabilityZone;

        /**
         *
         */
        private Boolean alive;

        private Object configurations;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static final class Configurations{

            private String dhcpDriver;

            private Long dhcpLeaseDuration;

            private Boolean logAgentHeartbeats;

            private Integer networks;

            private Integer ports;

            private Integer subnets;
        }
    }
}
