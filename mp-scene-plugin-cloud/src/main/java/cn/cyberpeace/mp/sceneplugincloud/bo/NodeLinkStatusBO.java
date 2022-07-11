package cn.cyberpeace.mp.sceneplugincloud.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/7/8
 */
@Getter
@Setter
public class NodeLinkStatusBO {
    @ApiModelProperty("节点id")
    private String nodeId;
    @JsonIgnore
    private String resourceId;
    @ApiModelProperty("节点状态,0:离线,1:在线")
    private Integer status;
    @ApiModelProperty("端口连接状态列表")
    private List<ConnectionStatus> connectionStatusList;

    public NodeLinkStatusBO(String nodeId) {
        this.nodeId = nodeId;
        this.connectionStatusList = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class ConnectionStatus {
        @ApiModelProperty("端口id")
        private String connectionId;
        @ApiModelProperty("端口连接状态,0:离线,1:在线")
        private Integer status;
        @JsonIgnore
        private String networkId;

        public ConnectionStatus(String connectionId) {
            this.connectionId = connectionId;
        }
    }
}
