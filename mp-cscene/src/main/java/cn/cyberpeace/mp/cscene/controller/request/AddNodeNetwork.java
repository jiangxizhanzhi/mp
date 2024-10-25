package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;

@Data
public class AddNodeNetwork {
    @ApiModelProperty(value = "场景id")
    private String sceneId;
    @ApiModelProperty("节点id,当前场景内唯一")
    private String nodeId;
    @ApiModelProperty("需要加入的网络id")
    private String networkId;
    @ApiModelProperty("指定ip")
    private String ip ;
}