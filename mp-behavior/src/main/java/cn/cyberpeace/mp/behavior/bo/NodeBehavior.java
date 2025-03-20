package cn.cyberpeace.mp.behavior.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class NodeBehavior implements Serializable {

	@ApiModelProperty("场景id")
	private String sceneId;

	@ApiModelProperty("节点id")
	private String nodeId;

	@ApiModelProperty("用户行为配置")
	private List<NodeUserBehavior> userBehaviorList;
}
