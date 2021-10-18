package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NetworkAgentModel{

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
