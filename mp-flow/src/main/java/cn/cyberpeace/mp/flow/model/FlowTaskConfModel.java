package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 流量任务配置对象
 * @Author qiaokang
 * @Date 2023-04-27 09:21:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_task_conf")
public class FlowTaskConfModel extends BaseModel {

	/**
	 * 任务id
	 */
	@ApiModelProperty(value = "任务id")
	@DefaultStr
	private String taskId;

	/**
	 * 持续运行：0 否，1 是
	 */
	@ApiModelProperty(value = "持续运行：0 否，1 是")
	private Integer keepRunning;

	/**
	 * 速率无限制：0 否，1 是
	 */
	@ApiModelProperty(value = "速率无限制：0 否，1 是")
	private Integer rateUnlimited;

	/**
	 * 是否流量重放：0 否，1 是
	 */
	@ApiModelProperty(value = "是否流量重放：0 否，1 是")
	private Integer replay;

	/**
	 * 配置
	 */
	@ApiModelProperty(value = "配置")
	@DefaultStr
	private String conf;


}
