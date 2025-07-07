 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowTaskTaskModel.java.
 // File size 2515.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName FlowTaskTaskModel
 * @Description 流量执行任务对象
 * @Author qiaokang
 * @Date 2023-04-27 09:21:37
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_task_task")
public class FlowTaskTaskModel extends BaseModel {

	/**
	 * 场景实例id
	 */
	@ApiModelProperty(value = "场景实例id")
	@DefaultStr
	private String sceneId;

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

	@ApiModelProperty(value = "流量名称")
	@DefaultStr
	private String flowName;

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
	@DefaultInt
	private Integer successCount;

	/**
	 * 失败执行次数
	 */
	@ApiModelProperty(value = "失败执行次数")
	@DefaultInt
	private Integer errorCount;

	/**
	 * 任务状态: 0 初始化，1 正在运行，2 正在停止， 3 已停止, 4 失败
	 */
	@ApiModelProperty(value = "任务状态: 0 初始化，1 正在运行，2 正在停止， 3 已停止, 4 失败, 5 初始化中")
	@DefaultInt
	private Integer taskStatus;

	/**
	 * 错误信息
	 */
	@ApiModelProperty(value = "错误信息")
	@DefaultStr
	private String msg;

	/**
	 * 目标节点id
	 */
	@ApiModelProperty(value = "目标节点id")
	@DefaultStr
	private String nodeId;

	/**
	 * 目标节点ip
	 */
	@ApiModelProperty(value = "目标节点ip")
	@DefaultStr
	private String nodeIp;

	@ApiModelProperty(value = "目标节点网络")
	@DefaultStr
	private String nodeNetworkId ;

	/**
	 * 目标节点名称
	 */
	@ApiModelProperty(value = "目标节点名称")
	@DefaultStr
	private String nodeName;


}
