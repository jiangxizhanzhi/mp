 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NodeLinkStatusBO.java.
 // File size 1516.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName NodeLinkStatusBO
 * @Description
 * @Author maojun
 * @Date 2022/7/8
 * @Version 1.0.0
 */
@Getter
@Setter
public class NodeLinkStatusBO {
    @ApiModelProperty("节点id")
    private String nodeId;
    @JsonIgnore
    private String resourceId;
    @JsonIgnore
    private String defaultNetworkId;
    @ApiModelProperty("节点状态,0:离线,1:在线")
    private Integer status;
    @ApiModelProperty("连线状态列表")
    private List<ConnectionStatus> connectionStatusList;

    /**
     * NodeLinkStatusBO
     *
     * @param nodeId
     */
    public NodeLinkStatusBO(String nodeId) {
        this.nodeId = nodeId;
        this.connectionStatusList = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class ConnectionStatus {
        @ApiModelProperty("连线id")
        private String connectionId;
        @ApiModelProperty("连线状态,0:离线,1:在线")
        private Integer status;
        @JsonIgnore
        private String networkId;

        /**
         * ConnectionStatus
         *
         * @param connectionId
         */
        public ConnectionStatus(String connectionId) {
            this.connectionId = connectionId;
        }
    }
}
