package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CsceneInstanceNodesResetPasswordReq implements Serializable {

	@ApiModelProperty(value = "节点密码配置列表")
	private List<NodeResetPasswordReq> nodeReqs;

	@ApiModelProperty(value = "场景id")
	private String sceneId;

}
