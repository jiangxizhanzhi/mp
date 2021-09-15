package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "场景接入会话数据")
@Data
public class NodeSessionBO {
    @ApiModelProperty(value = "节点id")
    private String nodeId;
    @ApiModelProperty(value = "会话用户")
    private String user;
    @ApiModelProperty(value = "监控url")
    private String monitorUrl;
    @ApiModelProperty(value = "协助url")
    private String assistanceUrl;
}
