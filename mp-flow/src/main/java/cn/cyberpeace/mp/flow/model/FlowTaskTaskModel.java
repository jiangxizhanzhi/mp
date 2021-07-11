package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_task_task")
public class FlowTaskTaskModel extends BaseModel {

	
	@ApiModelProperty(value = "场景实例id")
	@DefaultStr
	private String sceneId;

	
	@ApiModelProperty(value = "流量执行器id")
	@DefaultStr
	private String actuatorId;

	
	@ApiModelProperty(value = "资源类型：0 pcap包，1 脚本")
	private Integer resourceType;

	
	@ApiModelProperty(value = "交互类型：0 背景流量，1 攻击流量")
	private Integer interactiveType;

	
	@ApiModelProperty(value = "流量id")
	@DefaultStr
	private String flowId;

	
	@ApiModelProperty(value = "网口id")
	@DefaultStr
	private String portId;

	
	@ApiModelProperty(value = "成功执行次数")
	@DefaultInt
	private Integer successCount;

	
	@ApiModelProperty(value = "失败执行次数")
	@DefaultInt
	private Integer errorCount;

	
	@ApiModelProperty(value = "任务状态: 0 初始化，1 正在运行，2 正在停止， 3 已停止, 4 失败")
	@DefaultInt
	private Integer taskStatus;

	
	@ApiModelProperty(value = "目标节点id")
	@DefaultStr
	private String nodeId;

	
	@ApiModelProperty(value = "目标节点ip")
	@DefaultStr
	private String nodeIp;

	
	@ApiModelProperty(value = "目标节点名称")
	@DefaultStr
	private String nodeName;


}
