package cn.cyberpeace.mp.sceneplugincloud.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "场景接入连接数据")
@Data
public class NodeConnectionBO {
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
    @ApiModelProperty(value = "会话协议")
    private String protocol;
    @ApiModelProperty(value = "连接id")
    private String connectionId;
    @ApiModelProperty(value = "连接url")
    private String connectionUrl;
    @ApiModelProperty(value = "是否开启录屏")
    private Boolean connectionEnableRecording;
    @ApiModelProperty(value = "录屏名称")
    private String connectionRecordingName;
}
