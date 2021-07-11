package cn.cyberpeace.mp.cscene.dto.scene;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel(description = "场景接入会话数据")
@Data
public class UserNodeSessionInfo {
    @ApiModelProperty(value = "场景id")
    private String sceneId;
    @ApiModelProperty(value = "场景名称")
    private String sceneName;
    @ApiModelProperty(value = "节点id")
    private String nodeId;
    @ApiModelProperty(value = "节点名称")
    private String nodeName;
    @ApiModelProperty(value = "会话用户")
    private String user;
    @ApiModelProperty(value = "会话用户名称")
    private String userName;
    @ApiModelProperty(value = "会话协议")
    private String protocol;
    @ApiModelProperty(value = "监控url")
    private String monitorUrl;
    @ApiModelProperty(value = "协助url")
    private String assistanceUrl;
}
