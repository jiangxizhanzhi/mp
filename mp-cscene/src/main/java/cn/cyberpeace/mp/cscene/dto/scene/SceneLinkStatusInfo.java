package cn.cyberpeace.mp.cscene.dto.scene;

import cn.cyberpeace.mp.sceneplugincloud.bo.NodeLinkStatusBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@ApiModel(description = "场景链路状态")
@Getter
@Setter
public class SceneLinkStatusInfo {
    @ApiModelProperty(value = "id")
    private String csceneId;
    @ApiModelProperty(value = "节点状态列表")
    private List<NodeLinkStatusBO> nodeLinkStatusList;
}