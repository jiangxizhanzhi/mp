package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 流量执行任务对象
 * @Author qiaokang
 * @Date 2023-04-27 09:21:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_task_task")
public class FlowTaskTaskModel extends BaseModel {

	/**
	 * 流量执行器id
	 */
	@ApiModelProperty(value = "流量执行器id")
	@DefaultStr
	private String actuatorId;

	/**
	 * 资源类型：0 pcap包，1 脚本
	 */
	@ApiModelProperty(value = "资源类型：0 pcap包，1 脚本")
	private Integer resourceType;

	/**
	 * 交互类型：0 背景流量，1 攻击流量
	 */
	@ApiModelProperty(value = "交互类型：0 背景流量，1 攻击流量")
	private Integer interactiveType;

	/**
	 * 流量id
	 */
	@ApiModelProperty(value = "流量id")
	@DefaultStr
	private String flowId;

	/**
	 * 网口id
	 */
	@ApiModelProperty(value = "网口id")
	@DefaultStr
	private String portId;

	/**
	 * 成功执行次数
	 */
	@ApiModelProperty(value = "成功执行次数")
	private Integer successCount;

	/**
	 * 失败执行次数
	 */
	@ApiModelProperty(value = "失败执行次数")
	private Integer errorCount;

	/**
	 * 任务状态: 0 初始化，1 正在运行，2 正在停止， 3 已停止, 4 失败
	 */
	@ApiModelProperty(value = "任务状态: 0 初始化，1 正在运行，2 正在停止， 3 已停止, 4 失败")
	private Integer taskStatus;

	/**
	 * 目标节点ip
	 */
	@ApiModelProperty(value = "目标节点ip")
	@DefaultStr
	private String nodeIp;

	/**
	 * 目标节点名称
	 */
	@ApiModelProperty(value = "目标节点名称")
	@DefaultStr
	private String nodeName;


}
