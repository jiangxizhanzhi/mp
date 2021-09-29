package cn.cyberpeace.mp.sceneplugincloud.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "场景接入会话数据")
@Data
public class NodeSessionBO {
    @ApiModelProperty(value = "节点id")
    private String nodeId;
    @ApiModelProperty(value = "节点名称")
    private String nodeName;
    @ApiModelProperty(value = "会话用户")
    private String user;
    @ApiModelProperty(value = "会话协议")
    private String protocol;
    @ApiModelProperty(value = "监控url")
    private String monitorUrl;
    @ApiModelProperty(value = "协助url")
    private String assistanceUrl;
}
