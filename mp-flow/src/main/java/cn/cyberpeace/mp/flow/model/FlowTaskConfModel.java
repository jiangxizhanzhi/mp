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
@Table("mp_flow_task_conf")
public class FlowTaskConfModel extends BaseModel {

	
	@ApiModelProperty(value = "任务id")
	@DefaultStr
	private String taskId;

	
	@ApiModelProperty(value = "持续运行：0 否，1 是")
	private Integer keepRunning;

	
	@ApiModelProperty(value = "速率无限制：0 否，1 是")
	private Integer rateUnlimited;

	
	@ApiModelProperty(value = "是否流量重放：0 否，1 是")
	private Integer replay;

	@ApiModelProperty(value = "是否开启流量重放和速率配置：0 否，1 是")
	@DefaultInt(value = 0)
	private Integer config;

	
	@ApiModelProperty(value = "配置")
	@DefaultStr
	private String conf;


}
