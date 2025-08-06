package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CsceneInstanceNodePortForwardingReq implements Serializable {

	@ApiModelProperty("节点id")
	private String nodeId;

	@ApiModelProperty("场景id")
	private String sceneId;

	@ApiModelProperty("源端口")
	private Integer sourcePort;

	@ApiModelProperty("目的端口")
	private Integer destPort;
}
