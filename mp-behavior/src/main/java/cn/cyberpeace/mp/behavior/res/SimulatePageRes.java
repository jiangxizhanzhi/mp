package cn.cyberpeace.mp.behavior.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SimulatePageRes implements Serializable {

	@ApiModelProperty("仪表id")
	private String id;

	@ApiModelProperty("场景名称(实验任务名称)")
	private String sceneName;

	@ApiModelProperty("节点名称(子仪表名称)")
	private String nodeName;

	@ApiModelProperty("状态")
	private Integer status;
}
