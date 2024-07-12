 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NetworkAgentModel.java.
 // File size 1663.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName NetworkAgentModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NetworkAgentModel implements Serializable {

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
