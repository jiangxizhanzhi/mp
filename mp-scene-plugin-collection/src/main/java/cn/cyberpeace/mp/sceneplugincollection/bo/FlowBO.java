package cn.cyberpeace.mp.sceneplugincollection.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class FlowBO {

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
    private List<String> sourceMac;

    @ApiModelProperty("目的Mac")
    private List<String> targetMac;

    @ApiModelProperty("源端口")
    private String sourcePort;

    @ApiModelProperty("目的端口")
    private String targetPort;

    @ApiModelProperty("协议类型")
    private List<String> protocol;

    @ApiModelProperty("流量大小")
    private Long size;

    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("时间戳毫秒级")
    private Long timestamp;

}
