package cn.cyberpeace.mp.behavior.req;

import cn.cyberpeace.mp.behavior.bo.NodeBo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CreateSimulateReq implements Serializable {

	@ApiModelProperty("场景模板id")
	private String sceneTemplateId;

	@ApiModelProperty("场景id")
	private String sceneId;

	@ApiModelProperty("场景名称")
	private String sceneName;

//	@ApiModelProperty("行为节点列表")
//	private List<NodeBo> behaviorNodeList;

	@ApiModelProperty("节点ip列表")
	private List<NodeBo> nodeList;
}
