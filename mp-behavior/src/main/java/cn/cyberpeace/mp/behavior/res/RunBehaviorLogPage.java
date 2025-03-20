package cn.cyberpeace.mp.behavior.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class RunBehaviorLogPage implements Serializable {

	@ApiModelProperty("行为名称")
	private String behaviorName;

	@ApiModelProperty("参数")
	private String params;

	@ApiModelProperty("执行日志")
	private String logContent;

	@ApiModelProperty("执行时间")
	private LocalDateTime runTime;
}
