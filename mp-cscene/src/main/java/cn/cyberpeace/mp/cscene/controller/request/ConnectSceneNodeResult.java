package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ConnectSceneNodeResult {
    @ApiModelProperty("连接的id")
    private String id;

    @ApiModelProperty("场景id")
    private String sceneId;

    @ApiModelProperty("节点id")
    private String nodeId;

    @ApiModelProperty("端口id")
    private String portId;

    @ApiModelProperty("外部节点所在的场景id")
    private String externalSceneId;

    @ApiModelProperty("外部节点的节点id")
    private String externalNodeId;

    @ApiModelProperty("给外部节点分配的ip")
    private String externalNodeIp ;
}