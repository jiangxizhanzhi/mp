package cn.cyberpeace.mp.behavior.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class NodeBo implements Serializable {

	@ApiModelProperty("节点id")
	private String nodeId;

	@ApiModelProperty("节点名称")
	private String nodeName;

	@ApiModelProperty("节点ip")
	private String nodeIp;
}
