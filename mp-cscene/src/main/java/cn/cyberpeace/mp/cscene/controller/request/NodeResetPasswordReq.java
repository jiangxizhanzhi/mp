package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NodeResetPasswordReq {

	@ApiModelProperty("节点id")
	private String nodeId;

	@ApiModelProperty("原root密码")
	private String oldPassword;

	@ApiModelProperty("新root密码")
	private String newPassword;
}
