package cn.cyberpeace.mp.behavior.req;

import cn.cyberpeace.mp.behavior.bo.Param;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
public class RunBehaviorReq implements Serializable {

	@ApiModelProperty("队列名称")
	@NotBlank(message = "队列名称不能为空")
	private String queueName;

	@ApiModelProperty("网络行为模板id")
	@NotBlank(message = "网络行为模板id不能为空")
	private String behaviorTemplateId;

	@ApiModelProperty("参数配置")
	private List<Param> paramList;
}
