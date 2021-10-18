package cn.cyberpeace.mp.sceneplugincollection.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionFlowBO {

    @ApiModelProperty("场景id")
    private String sceneId;

    @ApiModelProperty("源节点")
    private String sourceNodeId;

    @ApiModelProperty("目的节点")
    private String targetNodeId;

    @ApiModelProperty("源节点名称")
    private String sourceNodeName;

    @ApiModelProperty("目的节点名称")
    private String targetNodeName;

    @ApiModelProperty("源IP")
    private String sourceIp;

    @ApiModelProperty("目的IP")
    private String targetIp;

    @ApiModelProperty("源Mac")
    private String sourceMac;

    @ApiModelProperty("目的Mac")
    private String targetMac;

    @ApiModelProperty("源端口")
    private String sourcePort;

    @ApiModelProperty("目的端口")
    private String targetPort;

    @ApiModelProperty("协议类型")
    private String protocol;

    @ApiModelProperty("流量大小")
    private Integer size;

    @ApiModelProperty("时间")
    private LocalDateTime time;

}
