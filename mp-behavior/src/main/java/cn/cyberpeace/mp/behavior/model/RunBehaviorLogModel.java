package cn.cyberpeace.mp.behavior.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_run_behavior_log")
public class RunBehaviorLogModel extends BaseModel {

	@ApiModelProperty("日志内容")
	private String logContent;

	@ApiModelProperty("行为模板id")
	private String behaviorTemplateId;

	@ApiModelProperty("参数配置")
	private String params;

	@ApiModelProperty("队列名称")
	private String queueName;

	@ApiModelProperty("行为模板名称")
	private String behaviorTemplateName;

}
