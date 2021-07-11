package cn.cyberpeace.mp.sceneplugincloud.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


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

        public ConnectionStatus(String connectionId) {
            this.connectionId = connectionId;
        }
    }
}
